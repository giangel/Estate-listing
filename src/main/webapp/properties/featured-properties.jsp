<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctx = request.getContextPath();
    com.realestate.dao.PropertyDAO pDAO =
        new com.realestate.dao.PropertyDAO();
    request.setAttribute("featuredProps", pDAO.findFeatured(12));
%>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-4">

  <div class="re-section-header">
    <span class="section-eyebrow">Editor's Choice</span>
    <h2>Featured Properties</h2>
    <p>
      Hand-picked verified listings offering the best value, location,
      and facilities near AOPE campus.
    </p>
  </div>

  <c:choose>
    <c:when test="${not empty featuredProps}">
      <div class="row g-4">
        <c:forEach var="prop" items="${featuredProps}">
          <div class="col-lg-4 col-md-6">
            <jsp:include page="/includes/property-card.jsp" />
          </div>
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise>
      <div class="empty-state">
        <div class="empty-icon"><i class="bi bi-star"></i></div>
        <h4>No Featured Properties</h4>
        <p>
          No properties are currently featured.
          Browse all available listings instead.
        </p>
        <a href="<%= ctx %>/properties" class="btn btn-primary-brand">
          <i class="bi bi-search me-2"></i> Browse All Properties
        </a>
      </div>
    </c:otherwise>
  </c:choose>

</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
<script src="<%= ctx %>/assets/js/compare.js"></script>
</body>
</html>