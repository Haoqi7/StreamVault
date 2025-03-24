(function() {
	setTimeout(function(){
		$('#viewbox_report > h1').before('<font class="pushMessage">推送</font>')
		pushMessage();
	},2500)


	function pushMessage(){
		$('.pushMessage').click(function(){
			chrome.runtime.sendMessage({Message: window.location.href}, function (response) {
				alert(response);
			})
		})
	}
})();
