<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<jsp:include page="/includes/navbar.jsp" />

<div class="container py-5">
  <div class="row">

    <div class="col-lg-3 mb-4">
      <div class="filter-sidebar">
        <div class="filter-title">
          <i class="bi bi-house-lock"></i> Landlord Portal
        </div>
        <nav class="d-flex flex-column gap-1">
          <a href="<%= ctx %>/landlord/dashboard"
             class="re-sidebar-link active" style="color:var(--re-primary)">
            <i class="bi bi-speedometer2"></i> Dashboard
          </a>
          <a href="<%= ctx %>/landlord/create-property"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-plus-circle"></i> Add Property
          </a>
          <a href="<%= ctx %>/landlord/manage-properties.jsp"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-building"></i> My Properties
          </a>
          <a href="<%= ctx %>/user/inquiry"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-envelope"></i> Inquiries
          </a>
          <a href="<%= ctx %>/user/appointment"
             class="re-sidebar-link" style="color:var(--re-gray-700)">
            <i class="bi bi-calendar"></i> Appointments
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

    <div class="col-lg-9">

      <%@ include file="/includes/alerts.jsp" %>

      <div class="p-4 rounded-4 mb-4"
           style="background:linear-gradient(135deg,#e8a020,#c8851a);color:white">
        <h3 class="mb-1">
          Welcome, <c:out value="${sessionScope.userName}" default="Landlord"/>!
        </h3>
        <p style="opacity:0.9;margin:0">
          Manage your property listings and track inquiries from your dashboard.
        </p>
        <a href="<%= ctx %>/landlord/create-property"
           class="btn mt-3"
           style="background:white;color:#e8a020;font-weight:700">
          <i class="bi bi-plus-circle me-1"></i> List a New Property
        </a>
      </div>

      <div class="row g-3 mb-4">
        
        <div class="col-md-3">
          <div class="stat-card" style="--stat-color:#1a3c5e">
            <div class="stat-icon">
              <i class="bi bi-building"></i>
            </div>
            <div class="stat-info">
              <div class="stat-num">
                <c:out value="${requestScope.totalListings}" default="0"/>
              </div>
              <div class="stat-label">Total Listings</div>
            </div>
          </div>
        </div>
        
        <div class="col-md-3">
          <div class="stat-card" style="--stat-color:#198754">
            <div class="stat-icon" style="background:rgba(25,135,84,0.1)">
              <i class="bi bi-check-circle" style="color:#198754"></i>
            </div>
            <div class="stat-info">
              <div class="stat-num">
                <c:out value="${requestScope.activeListings}" default="0"/>
              </div>
              <div class="stat-label">Active</div>
            </div>
          </div>
        </div>
        
        <div class="col-md-3">
          <div class="stat-card" style="--stat-color:#0d6efd">
            <div class="stat-icon" style="background:rgba(13,110,253,0.1)">
              <i class="bi bi-envelope" style="color:#0d6efd"></i>
            </div>
            <div class="stat-info">
              <div class="stat-num">
                <c:out value="${requestScope.totalInquiries}" default="0"/>
              </div>
              <div class="stat-label">Inquiries</div>
            </div>
          </div>
        </div>
        
        <div class="col-md-3">
          <div class="stat-card" style="--stat-color:#fd7e14">
            <div class="stat-icon" style="background:rgba(253,126,20,0.1)">
              <i class="bi bi-hourglass" style="color:#fd7e14"></i>
            </div>
            <div class="stat-info">
              <div class="stat-num">
                <c:out value="${requestScope.pendingListings}" default="0"/>
              </div>
              <div class="stat-label">Pending</div>
            </div>
          </div>
        </div>
        
      </div>

      <div class="re-table-wrapper">
        <div class="re-table-header">
          <h5><i class="bi bi-building me-2"></i>Quick Actions</h5>
        </div>
        <div class="p-4">
          <div class="row g-3">
            <div class="col-md-4">
              <a href="<%= ctx %>/landlord/create-property"
                 class="d-block p-3 rounded-3 text-decoration-none text-center"
                 style="background:rgba(26,60,94,0.06); border:1.5px dashed var(--re-primary)">
                <i class="bi bi-plus-square" style="font-size:2rem;color:var(--re-primary)"></i>
                <div class="fw-bold mt-2" style="color:var(--re-primary)">
                  Add New Property
                </div>
              </a>
            </div>
            <div class="col-md-4">
              <a href="<%= ctx %>/landlord/manage-properties.jsp"
                 class="d-block p-3 rounded-3 text-decoration-none text-center"
                 style="background:rgba(25,135,84,0.06); border:1.5px dashed #198754">
                <i class="bi bi-buildings" style="font-size:2rem;color:#198754"></i>
                <div class="fw-bold mt-2" style="color:#198754">
                  Manage Listings
                </div>
              </a>
            </div>
            <div class="col-md-4">
              <a href="<%= ctx %>/user/inquiry"
                 class="d-block p-3 rounded-3 text-decoration-none text-center"
                 style="background:rgba(13,110,253,0.06); border:1.5px dashed #0d6efd">
                <i class="bi bi-envelope-open" style="font-size:2rem;color:#0d6efd"></i>
                <div class="fw-bold mt-2" style="color:#0d6efd">
                  View Inquiries
                </div>
              </a>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</div>

<jsp:include page="/includes/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
</body>
</html>