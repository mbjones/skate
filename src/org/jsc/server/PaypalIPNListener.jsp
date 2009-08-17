<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>PaypalIPNListener</title>
</head>
<body>

// Java JSP

<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.io.*" %>

<%
    // read post from PayPal system and add 'cmd'
    Enumeration en = request.getParameterNames();
    String str = "cmd=_notify-validate";
    while (en.hasMoreElements()) {
        String paramName = (String) en.nextElement();
        String paramValue = request.getParameter(paramName);
        str = str + "&" + paramName + "="
                + URLEncoder.encode(paramValue);
    }

    // post back to PayPal system to validate
    // NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
    // using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
    // and configured for older versions.
    URL u = new URL("https://www.paypal.com/cgi-bin/webscr");
    URLConnection uc = u.openConnection();
    uc.setDoOutput(true);
    uc.setRequestProperty("Content-Type",
            "application/x-www-form-urlencoded");
    PrintWriter pw = new PrintWriter(uc.getOutputStream());
    pw.println(str);
    pw.close();

    BufferedReader in = new BufferedReader(new InputStreamReader(uc
            .getInputStream()));
    String res = in.readLine();
    in.close();

    // assign posted variables to local variables
    String itemName = request.getParameter("item_name");
    String itemNumber = request.getParameter("item_number");
    String paymentStatus = request.getParameter("payment_status");
    String paymentAmount = request.getParameter("mc_gross");
    String paymentCurrency = request.getParameter("mc_currency");
    String txnId = request.getParameter("txn_id");
    String receiverEmail = request.getParameter("receiver_email");
    String payerEmail = request.getParameter("payer_email");

    //check notification validation
    if (res.equals("VERIFIED")) {
        // check that paymentStatus=Completed
        // check that txnId has not been previously processed
        // check that receiverEmail is your Primary PayPal email
        // check that paymentAmount/paymentCurrency are correct
        // process payment
    } else if (res.equals("INVALID")) {
        // log for investigation
    } else {
        // error
    }
%>

</body>
</html>
