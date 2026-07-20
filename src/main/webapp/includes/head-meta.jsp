<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // REMOVED: contextPath variable to protect against duplicate variable scoping exceptions
    String rawTitle = (request.getAttribute("pageTitle") != null)
                      ? (String) request.getAttribute("pageTitle")
                      : "AOPE Real Estate";
    request.setAttribute("safePageTitle", rawTitle);
%>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Find affordable, verified accommodation near Adeseun Ogundoyin Polytechnic, Eruwa.">
<meta name="theme-color" content="#1a3c5e">

<title><c:out value="${safePageTitle}"/> | AOPE Real Estate</title>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css">

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dark-mode.css" id="darkModeStylesheet" disabled>