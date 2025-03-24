var service ="http://127.0.0.1:28081/api/processingVideos";
chrome.extension.onMessage.addListener(
  function(request, sender, sendResponse) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', service+'?token=123&video='+request.Message);
    xhr.send();
    xhr.onreadystatechange = function () {
    if (xhr.readyState === 4 && xhr.status === 200) {
        var data = JSON.parse(xhr.responseText);
        if(data.resCode == "000001"){
          sendResponse(data);
        };
    }
    };
    sendResponse("已提交");
  });