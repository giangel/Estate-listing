<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctx = request.getContextPath();
    com.realestate.dao.UserDAO     uDAO = new com.realestate.dao.UserDAO();
    com.realestate.dao.PropertyDAO pDAO = new com.realestate.dao.PropertyDAO();
    com.realestate.dao.FraudReportDAO fDAO =
        new com.realestate.dao.FraudReportDAO();

    request.setAttribute("rptStudents",   uDAO.countByRole("STUDENT"));
    request.setAttribute("rptStaff",      uDAO.countByRole("STAFF"));
    request.setAttribute("rptLandlords",  uDAO.countByRole("LANDLORD"));
    request.setAttribute("rptAgents",     uDAO.countByRole("AGENT"));
    request.setAttribute("rptTotal",      uDAO.countTotal());
    request.setAttribute("rptListings",   pDAO.countTotal());
    request.setAttribute("rptAvailable",  pDAO.countAvailable());
    request.setAttribute("rptPending",    pDAO.countPending());
    request.setAttribute("rptVerified",   pDAO.countVerified());
    request.setAttribute("rptFraudOpen",  fDAO.countOpen());
    request.setAttribute("rptPopular",    pDAO.findPopular(10));
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="re-dashboard">
  <jsp:include page="/includes/admin-sidebar.jsp" />
  <main class="re-dashboard-content">

    <div class="dashboard-page-header">
      <h2><i class="bi bi-bar-chart-line me-2"></i>Reports &amp; Analytics</h2>
    </div>

    <!-- Summary Cards -->
    <div class="row g-3 mb-4">
      <div class="col-md-3">
        <div class="stat-card" style="--stat-color:#1a3c5e">
          <div class="stat-icon"><i class="bi bi-people"></i></div>
          <div class="stat-info">
            <div class="stat-num">${rptTotal}</div>
            <div class="stat-label">Total Users</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card" style="--stat-color:#198754">
          <div class="stat-icon" style="background:rgba(25,135,84,0.1)">
            <i class="bi bi-building-check" style="color:#198754"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num">${rptAvailable}</div>
            <div class="stat-label">Active Listings</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card" style="--stat-color:#0dcaf0">
          <div class="stat-icon" style="background:rgba(13,202,240,0.1)">
            <i class="bi bi-patch-check" style="color:#0dcaf0"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num">${rptVerified}</div>
            <div class="stat-label">Verified Listings</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-card" style="--stat-color:#dc3545">
          <div class="stat-icon" style="background:rgba(220,53,69,0.1)">
            <i class="bi bi-shield-exclamation" style="color:#dc3545"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num">${rptFraudOpen}</div>
            <div class="stat-label">Open Fraud Reports</div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-4">

      <!-- User Breakdown -->
      <div class="col-lg-6">
        <div class="re-table-wrapper p-3">
          <h5 class="mb-3">
            <i class="bi bi-people me-2"></i>User Breakdown
          </h5>
          <table class="re-table">
            <thead>
              <tr><th>Role</th><th>Count</th><th>%</th></tr>
            </thead>
            <tbody>
              <tr>
                <td>Students</td>
                <td><strong>${rptStudents}</strong></td>
                <td style="font-size:0.82rem;color:var(--re-gray-500)">
                  <c:if test="${rptTotal > 0}">
                    <fmt:formatNumber xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
                                      value="${rptStudents * 100 / rptTotal}"
                                      maxFractionDigits="1"/>%
                  </c:if>
                </td>
              </tr>
              <tr>
                <td>Staff</td>
                <td><strong>${rptStaff}</strong></td>
                <td style="font-size:0.82rem;color:var(--re-gray-500)">
                  <c:if test="${rptTotal > 0}">
                    <fmt:formatNumber xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
                                      value="${rptStaff * 100 / rptTotal}"
                                      maxFractionDigits="1"/>%
                  </c:if>
                </td>
              </tr>
              <tr>
                <td>Landlords</td>
                <td><strong>${rptLandlords}</strong></td>
                <td style="font-size:0.82rem;color:var(--re-gray-500)">
                  <c:if test="${rptTotal > 0}">
                    <fmt:formatNumber xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
                                      value="${rptLandlords * 100 / rptTotal}"
                                      maxFractionDigits="1"/>%
                  </c:if>
                </td>
              </tr>
              <tr>
                <td>Agents</td>
                <td><strong>${rptAgents}</strong></td>
                <td style="font-size:0.82rem;color:var(--re-gray-500)">
                  <c:if test="${rptTotal > 0}">
                    <fmt:formatNumber xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
                                      value="${rptAgents * 100 / rptTotal}"
                                      maxFractionDigits="1"/>%
                  </c:if>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Listing Breakdown -->
      <div class="col-lg-6">
        <div class="re-table-wrapper p-3">
          <h5 class="mb-3">
            <i class="bi bi-building me-2"></i>Listing Breakdown
          </h5>
          <table class="re-table">
            <thead>
              <tr><th>Category</th><th>Count</th></tr>
            </thead>
            <tbody>
              <tr>
                <td>Total Listings</td>
                <td><strong>${rptListings}</strong></td>
              </tr>
              <tr>
                <td>Available (Active)</td>
                <td>
                  <strong style="color:#198754">${rptAvailable}</strong>
                </td>
              </tr>
              <tr>
                <td>Pending Approval</td>
                <td>
                  <strong style="color:#fd7e14">${rptPending}</strong>
                </td>
              </tr>
              <tr>
                <td>Verified by Admin</td>
                <td>
                  <strong style="color:#0dcaf0">${rptVerified}</strong>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Top 10 Popular Properties -->
      <div class="col-12">
        <div class="re-table-wrapper">
          <div class="re-table-header">
            <h5>
              <i class="bi bi-fire me-2 text-warning"></i>
              Top 10 Most Popular Properties
            </h5>
          </div>
          <div class="table-responsive">
            <table class="re-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Property</th>
                  <th>Price</th>
                  <th>Views</th>
                  <th>Saves</th>
                  <th>Inquiries</th>
                  <th>Popularity Score</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="prop" items="${rptPopular}" varStatus="st">
                  <tr>
                    <td style="font-weight:800;color:var(--re-secondary)">
                      #${st.index + 1}
                    </td>
                    <td style="font-weight:600;font-size:0.875rem">
                      <a href="<%= ctx %>/property?id=${prop.propertyId}"
                         target="_blank"
                         style="color:var(--re-primary)">
                        <c:out value="${prop.title}"/>
                      </a>
                    </td>
                    <td style="color:var(--re-secondary);font-weight:600">
                      <c:out value="${prop.formattedPrice}"/>
                    </td>
                    <td>${prop.viewCount}</td>
                    <td>${prop.saveCount}</td>
                    <td>${prop.inquiryCount}</td>
                    <td>
                      <strong style="color:var(--re-primary)">
                        ${prop.popularityScore}
                      </strong>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </div>

    </div>
  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>