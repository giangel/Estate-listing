<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-4">

  <div class="dashboard-page-header">
    <div>
      <h2>
        <i class="bi bi-clock-history me-2"
           style="color:var(--re-secondary)"></i>
        Recently Viewed
      </h2>
      <p class="text-muted mb-0">
        Properties you have viewed recently (last 10).
      </p>
    </div>
  </div>

  <%@ include file="/includes/alerts.jsp" %>

  <c:choose>
    <c:when test="${not empty recentProperties}">
      <div class="row g-4">
        <c:forEach var="prop" items="${recentProperties}">
          <div class="col-lg-4 col-md-6">
            <jsp:include page="/includes/property-card.jsp" />
          </div>
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise>
      <div class="empty-state">
        <div class="empty-icon">
          <i class="bi bi-clock-history"></i>
        </div>
        <h4>No Viewing History</h4>
        <p>Properties you view will appear here.</p>
        <a href="<%= ctx %>/properties"
           class="btn btn-primary-brand">
          <i class="bi bi-search me-2"></i> Browse Properties
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