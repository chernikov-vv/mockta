<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
  <title>Signing in...</title>
  <script type="text/javascript">

    /**
     * Check whether this page is loaded in an iframe or a popup.
     */
    function isInIframe() {
      try {
        return window.self !== window.top;
      } catch (e) {
        return true;
      }
    }

    /**
     * Create the message payload to send back to the parent window.
     */
    function buildMessageData() {

      var data = {};

      <c:if test="${auth.state ne null}">
      data.state = '${auth.state}';
      </c:if>

      <c:if test="${auth.error ne null}">
      data.error = '${auth.error}';
      </c:if>

      <c:if test="${auth.errorDescription ne null}">
      data.error_description = '${auth.errorDescription}';
      </c:if>

      <c:if test="${auth.idToken ne null}">
      data.id_token = '${auth.idToken}';
      </c:if>

      return data;
    }

    /**
     * On window load, post the message payload back to the parent window.
     */
    function onWindowLoadHandler() {
      var parentWindow = window.isInIframe() ? window.parent : window.opener;

      <c:if test="${auth.errorDescription ne null}">
      if (window.console) {
        console.error('${auth.errorDescription}');
      }
      </c:if>

      parentWindow.postMessage(buildMessageData(), '${auth.frameUrl}');
    }

    // Attach event handler for the 'DOM loaded' or equivalent event on the window
    if (window.addEventListener) {
      window.addEventListener('DOMContentLoaded', onWindowLoadHandler, false);
    } else if (window.attachEvent) {
      // IE8
      window.attachEvent('onload', onWindowLoadHandler);
    }
  </script>
</head>
<body>

</body>
</html>