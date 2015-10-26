
var clicks = 0

function sendCount(){
	/*toString MUST be called on the callbackFunc function object or the
	 *JSON library will strip the function out of the message.
	 *This means that named or anonymous functions can be used but anonymous functions
	 *can not be treated as closures. The do will retain scope information for later execution.
	 *The anonymous function will not 'capture' values from the scope of the containing function.
	 */
	var message = {"cmd":"increment","count":clicks,"callbackFunc":function(responseAsJSON){

		var response = JSON.parse(responseAsJSON)
		clicks = response['count']
		document.querySelector("#messages_from_java").innerText = "Count is "+clicks
		}.toString()
	}
	var messageAsString = JSON.stringify(message)
	native.postMessage(messageAsString)

}

