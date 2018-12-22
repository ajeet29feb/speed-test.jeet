/* <![CDATA[*/
var syncTaskPointer = null;
var requestsQueue = [];
var requestResolveCallbackQueue = [];
var contentType = 'application/octet-stream';
var isDone = false;
var a;
var blob;

function nativeAjax(requestObj) {
    // this is your actual ajax function
    // which will return a promise

    // after finishing the ajax call you call the .next() function of syncRunner
    // you need to put it in the suceess callback or in the .then of the promise
    var start_time = new Date().getTime();
    $.ajax(requestObj).then(function (responseData) {
    	 var timeTaken = (new Date().getTime() - start_time);
        (requestResolveCallbackQueue.shift())(responseData);
        syncTaskPointer.next();
        console.log(responseData.length);       
        console.log('This request took ' + timeTaken + ' ms');
        var down = (1000 * responseData.length / timeTaken) / (1024 * 1024);
        var rDown = +(Math.round(down + "e+2") + "e-2");
        $("#speed-value").text(rDown);
        console.log('Down Speed is ' + rDown + ' MBPS');

        if (!isDone) {
            document.getElementById("file-content").innerHTML = responseData;
            document.getElementById("upload-btn").style.display = 'block';
            isDone = true;
        }

    });
}

function nativeAjaxUp(requestObj) {
    var file = document.getElementById("file-content").innerHTML;
    var start_time = new Date().getTime();
    $.ajax({
        method: "POST",
        url: requestObj,
        contentType: 'application/txt',
        data: file
    }).done(function (responseData) {
        (requestResolveCallbackQueue.shift())(responseData);
        syncTaskPointer.next();
        console.log(file.length);
        var timeTaken = (new Date().getTime() - start_time);
        console.log('This request took ' + timeTaken + ' ms');
        var upSpeed = (1000 * file.length / timeTaken) / (1024 * 1024);
        var rupSpeed = +(Math.round(upSpeed + "e+2") + "e-2");
        $("#speed-value-up").text(rupSpeed);
        console.log('Up Speed is ' + rupSpeed + ' MBPS');
    });
}

function* syncRunner(isDownload) {
    while (requestsQueue.length > 0) {
        if (isDownload) {
            yield nativeAjax(requestsQueue.shift());
        } else {
            yield nativeAjaxUp(requestsQueue.shift());
        }
    }

    // set the pointer to null
    syncTaskPointer = null;
    console.log("all resolved");
};

ajaxSync = function (isDownload, requestObj) {
    requestsQueue.push(requestObj);
    if (!syncTaskPointer) {
        syncTaskPointer = syncRunner(isDownload);
        syncTaskPointer.next();
    }
    return new Promise(function (resolve, reject) {
        var responseFlagFunc = function (data) {
            resolve(data);
        }
        requestResolveCallbackQueue.push(responseFlagFunc);
    });
}

function getUploaded() {
    document.getElementById("main2").style.display = 'block';
    var i;
    for (i = 0; i < 20; i++) {
    	// Prod
        ajaxSync(false, "https://tanvi-speedtest.azurewebsites.net/upload");
    	// local
    	// ajaxSync(false, "http://localhost:8080/upload");
    }
}

function getFileSize() {
    var i;
    for (i = 0; i < 20; i++) {
    	// Prod
        ajaxSync(true, "https://tanvi-speedtest.azurewebsites.net/downloadFile");
    	// local
        // ajaxSync(true, "http://localhost:8080/downloadFile");
    }
}

$(window).load(function(){getFileSize(); })

/* ]]> */
