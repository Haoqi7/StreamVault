javascript:var xhr = new XMLHttpRequest();
xhr.open('GET', 'http://127.0.0.1:28081/api/processingVideos?token=123&video='+window.location.href);
xhr.send();
xhr.onreadystatechange = function () {
if (xhr.readyState === 4 && xhr.status === 200) {
    var data = JSON.parse(xhr.responseText);
    if(data.resCode == "000001"){
      alert(data.message);  
    };
}
};