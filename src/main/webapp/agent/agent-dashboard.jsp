<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-5">
  <div class="row">
    <!-- Sidebar -->
    <div class="col-lg-3 mb-4">
      <div class="filter-sidebar">
        <div class="filter-title">
          <i class="bi bi-briefcase"></i> Agent Portal
        </div>
        <nav class="d-flex flex-column gap-1">
          <a href="<%= ctx %>/agent/agent-dashboard.jsp"
             class="re-sidebar-link active"
             style="color:var(--re-primary)">
            <i class="bi bi-speedometer2"></i> Dashboard
          </a>
<a href="<%= ctx %>/landlord/create-property"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-plus-circle"></i> Add Property
          </a>
          <a href="<%= ctx %>/landlord/manage-properties.jsp"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-building"></i> Manage Listings
          </a>
          <a href="<%= ctx %>/agent/verification-status.jsp"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-patch-check"></i> Verification Status
          </a>
          <a href="<%= ctx %>/user/inquiry"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-envelope"></i> Inquiries
          </a>
          <a href="<%= ctx %>/user/profile"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-person-gear"></i> Profile
          </a>
          <div class="divider my-1"></div>
          <a href="<%= ctx %>/logout"
             class="re-sidebar-link" style="color:var(--re-danger)"
             onclick="return confirm('Confirm logout?')">
            <i class="bi bi-box-arrow-right"></i> Logout
          </a>
        </nav>
      </div>
    </div>

    <!-- Main -->
    <div class="col-lg-9">
      <%@ include file="/includes/alerts.jsp" %>

      <!-- Welcome Banner -->
      <div class="p-4 rounded-4 mb-4"
           style="background:linear-gradient(135deg,#20c997,#0f9c74);
                  color:white">
        <h3 class="mb-1">
          Welcome, <c:out value="${sessionScope.userName}"/>!
        </h3>
        <p style="opacity:0.9;margin:0">
          Manage property listings on behalf of landlords.
        </p>
        <a href="<%= ctx %>/landlord/create-property"
           class="btn mt-3"
           style="background:white;color:#0f9c74;font-weight:700">
          <i class="bi bi-plus-circle me-1"></i> Add New Listing
        </a>
      </div>

      <!-- Quick Actions -->
      <div class="row g-3">
        <div class="col-md-4">
          <a href="<%= ctx %>/landlord/create-property"
             class="d-block p-3 rounded-3 text-decoration-none text-center"
             style="background:rgba(32,201,151,0.08);
                    border:1.5px dashed #20c997">
            <i class="bi bi-plus-square"
               style="font-size:2rem;color:#20c997"></i>
            <div class="fw-bold mt-2" style="color:#20c997">
              Add Property
            </div>
          </a>
        </div>
        <div class="col-md-4">
          <a href="<%= ctx %>/landlord/manage-properties.jsp"
             class="d-block p-3 rounded-3 text-decoration-none text-center"
             style="background:rgba(26,60,94,0.06);
                    border:1.5px dashed var(--re-primary)">
            <i class="bi bi-buildings"
               style="font-size:2rem;color:var(--re-primary)"></i>
            <div class="fw-bold mt-2" style="color:var(--re-primary)">
              My Listings
            </div>
          </a>
        </div>
        <div class="col-md-4">
          <a href="<%= ctx %>/agent/verification-status.jsp"
             class="d-block p-3 rounded-3 text-decoration-none text-center"
             style="background:rgba(255,193,7,0.08);
                    border:1.5px dashed #ffc107">
            <i class="bi bi-patch-check"
               style="font-size:2rem;color:#ffc107"></i>
            <div class="fw-bold mt-2" style="color:#664d03">
              Verify Status
            </div>
          </a>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>