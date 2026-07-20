<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="/includes/head-meta.jsp" />
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="re-dashboard">
<jsp:include page="/includes/admin-sidebar.jsp" />
  <main class="re-dashboard-content">

    <div class="dashboard-page-header">
      <div>
        <h2><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</h2>
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item">
              <a href="<%= ctx %>/index.jsp">Home</a>
            </li>
            <li class="breadcrumb-item active">Admin Dashboard</li>
          </ol>
        </nav>
      </div>
      <div class="d-flex gap-2">
        <a href="<%= ctx %>/admin/manage-properties?status=PENDING"
           class="btn btn-secondary-brand btn-sm-custom">
          <i class="bi bi-hourglass-split me-1"></i>
          Pending (<c:out value="${pendingListings}"/>)
        </a>
        <a href="<%= ctx %>/admin/reports"
           class="btn btn-outline-brand btn-sm-custom">
          <i class="bi bi-download me-1"></i> Reports
        </a>
      </div>
    </div>

    <%@ include file="/includes/alerts.jsp" %>

    <div class="row g-3 mb-4">
      <div class="col-xl-3 col-md-6">
        <div class="stat-card" style="--stat-color:#1a3c5e">
          <div class="stat-icon">
            <i class="bi bi-people-fill"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num"><c:out value="${totalUsers}"/></div>
            <div class="stat-label">Total Users</div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="stat-card" style="--stat-color:#198754">
          <div class="stat-icon" style="background:rgba(25,135,84,0.1)">
            <i class="bi bi-building-check" style="color:#198754"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num"><c:out value="${activeListings}"/></div>
            <div class="stat-label">Active Listings</div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="stat-card" style="--stat-color:#fd7e14">
          <div class="stat-icon" style="background:rgba(253,126,20,0.1)">
            <i class="bi bi-hourglass-split" style="color:#fd7e14"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num"><c:out value="${pendingListings}"/></div>
            <div class="stat-label">Pending Approval</div>
          </div>
        </div>
      </div>
      <div class="col-xl-3 col-md-6">
        <div class="stat-card" style="--stat-color:#dc3545">
          <div class="stat-icon" style="background:rgba(220,53,69,0.1)">
            <i class="bi bi-shield-exclamation" style="color:#dc3545"></i>
          </div>
          <div class="stat-info">
            <div class="stat-num"><c:out value="${openFraudReports}"/></div>
            <div class="stat-label">Open Fraud Reports</div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-xl-2 col-md-4 col-6">
        <div class="stat-card" style="--stat-color:#0d6efd">
          <div class="stat-info">
            <div class="stat-num" style="font-size:1.5rem"><c:out value="${totalStudents}"/></div>
            <div class="stat-label">Students</div>
          </div>
        </div>
      </div>
      <div class="col-xl-2 col-md-4 col-6">
        <div class="stat-card" style="--stat-color:#6610f2">
          <div class="stat-info">
            <div class="stat-num" style="font-size:1.5rem"><c:out value="${totalStaff}"/></div>
            <div class="stat-label">Staff</div>
          </div>
        </div>
      </div>
      <div class="col-xl-2 col-md-4 col-6">
        <div class="stat-card" style="--stat-color:#e8a020">
          <div class="stat-info">
            <div class="stat-num" style="font-size:1.5rem"><c:out value="${totalLandlords}"/></div>
            <div class="stat-label">Landlords</div>
          </div>
        </div>
      </div>
      <div class="col-xl-2 col-md-4 col-6">
        <div class="stat-card" style="--stat-color:#20c997">
          <div class="stat-info">
            <div class="stat-num" style="font-size:1.5rem"><c:out value="${totalAgents}"/></div>
            <div class="stat-label">Agents</div>
          </div>
        </div>
      </div>
      <div class="col-xl-2 col-md-4 col-6">
        <div class="stat-card" style="--stat-color:#198754">
          <div class="stat-info">
            <div class="stat-num" style="font-size:1.5rem"><c:out value="${verifiedListings}"/></div>
            <div class="stat-label">Verified Listings</div>
          </div>
        </div>
      </div>
      <div class="col-xl-2 col-md-4 col-6">
        <div class="stat-card" style="--stat-color:#1a3c5e">
          <div class="stat-info">
            <div class="stat-num" style="font-size:1.5rem"><c:out value="${totalListings}"/></div>
            <div class="stat-label">Total Listings</div>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-3 mb-4">
      <div class="col-lg-8">
        <div class="re-table-wrapper p-3">
          <div class="re-table-header">
            <h5><i class="bi bi-bar-chart me-2"></i>Platform Overview</h5>
          </div>
          <canvas id="platformChart" height="200"></canvas>
        </div>
      </div>
      <div class="col-lg-4">
        <div class="re-table-wrapper p-3">
          <div class="re-table-header">
            <h5><i class="bi bi-pie-chart me-2"></i>User Roles</h5>
          </div>
          <canvas id="userRoleChart"></canvas>
        </div>
      </div>
    </div>

    <div class="re-table-wrapper mb-4">
      <div class="re-table-header">
        <h5>
          <i class="bi bi-hourglass-split me-2 text-warning"></i>
          Pending Property Approvals
        </h5>
        <a href="<%= ctx %>/admin/manage-properties?status=PENDING"
           class="btn btn-sm btn-outline-brand">
          View All
        </a>
      </div>
      <div class="table-responsive">
        <table class="re-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Property Title</th>
              <th>Owner</th>
              <th>Type</th>
              <th>Price</th>
              <th>Distance</th>
              <th>Submitted</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${not empty pendingProperties}">
                <c:forEach var="prop" items="${pendingProperties}" end="4">
                  <tr>
                    <td><c:out value="${prop.propertyId}"/></td>
                    <td>
                      <a href="<%= ctx %>/property?id=${prop.propertyId}"
                         target="_blank" style="font-weight:600">
                        <c:out value="${prop.title}"/>
                      </a>
                    </td>
                    <td><c:out value="${prop.ownerName}"/></td>
                    <td><c:out value="${prop.typeName}"/></td>
                    <td class="fw-bold" style="color:var(--re-secondary)">
                      <c:out value="${prop.formattedPrice}"/>
                    </td>
                    <td>
                      <span class="re-badge re-badge-distance">
                        <c:out value="${prop.campusDistanceLabel}"/>
                      </span>
                    </td>
                    <td style="font-size:0.8rem;color:var(--re-gray-500)">
                      <fmt:formatDate value="${prop.createdAt}"
                                      pattern="dd MMM yyyy"/>
                    </td>
                    <td>
                      <div class="d-flex gap-1">
                        <form action="<%= ctx %>/admin/approve-property"
                              method="POST" novalidate style="display:inline">
                          <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                          <input type="hidden" name="propertyId"
                                 value="<c:out value='${prop.propertyId}'/>">
                          <input type="hidden" name="action" value="approve">
                          <button type="submit"
                                  class="btn btn-sm"
                                  style="background:#198754;color:white;
                                         padding:4px 10px;font-size:0.78rem"
                                  onclick="return confirm('Approve this listing?')">
                            <i class="bi bi-check-lg"></i> Approve
                          </button>
                        </form>
                        <form action="<%= ctx %>/admin/approve-property"
                              method="POST" style="display:inline">
                          <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                          <input type="hidden" name="propertyId"
                                 value="<c:out value='${prop.propertyId}'/>">
                          <input type="hidden" name="action" value="reject">
                          <button type="submit"
                                  class="btn btn-sm"
                                  style="background:#dc3545;color:white;
                                         padding:4px 10px;font-size:0.78rem"
                                  onclick="return confirm('Reject this listing?')">
                            <i class="bi bi-x-lg"></i> Reject
                          </button>
                        </form>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <tr>
                  <td colspan="8" class="text-center py-4 text-muted">
                    <i class="bi bi-check-circle-fill me-2 text-success"></i>
                    No pending approvals. All listings are up to date!
                  </td>
                </tr>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </div>

    <div class="re-table-wrapper mb-4">
      <div class="re-table-header">
        <h5>
          <i class="bi bi-journal-text me-2"></i>Recent System Activity
        </h5>
        <a href="<%= ctx %>/admin/audit-logs.jsp"
           class="btn btn-sm btn-outline-brand">View All Logs</a>
      </div>
      <div class="table-responsive">
        <table class="re-table">
          <thead>
            <tr>
              <th>User</th>
              <th>Action</th>
              <th>Entity</th>
              <th>Description</th>
              <th>IP Address</th>
              <th>Time</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="log" items="${recentAuditLogs}">
              <tr>
                <td style="font-size:0.82rem">
                  <c:out value="${not empty log.userName
                                 ? log.userName : 'System'}"/>
                </td>
                <td>
                  <code style="background:rgba(26,60,94,0.08);
                               padding:2px 6px;border-radius:4px;
                               font-size:0.75rem">
                    <c:out value="${log.action}"/>
                  </code>
                </td>
                <td style="font-size:0.8rem">
                  <c:out value="${log.entityType}"/>
                  <c:if test="${log.entityId > 0}">
                    #<c:out value="${log.entityId}"/>
                  </c:if>
                </td>
                <td style="font-size:0.8rem;color:var(--re-gray-700)">
                  <c:out value="${log.description}"/>
                </td>
                <td style="font-size:0.78rem;color:var(--re-gray-500)">
                  <c:out value="${log.ipAddress}"/>
                </td>
                <td style="font-size:0.78rem;color:var(--re-gray-500);
                           white-space:nowrap">
                  <fmt:formatDate value="${log.createdAt}"
                                  pattern="dd/MM HH:mm"/>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>

    <div class="re-table-wrapper mb-4">
      <div class="re-table-header">
        <h5>
          <i class="bi bi-fire me-2 text-warning"></i>Most Popular Properties
        </h5>
      </div>
      <div class="table-responsive">
        <table class="re-table">
          <thead>
            <tr>
              <th>Property</th>
              <th>Views</th>
              <th>Saves</th>
              <th>Inquiries</th>
              <th>Score</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="prop" items="${popularProperties}">
              <tr>
                <td>
                  <a href="<%= ctx %>/property?id=${prop.propertyId}"
                     target="_blank" style="font-weight:600;font-size:0.875rem">
                    <c:out value="${prop.title}"/>
                  </a>
                </td>
                <td><c:out value="${prop.viewCount}"/></td>
                <td><c:out value="${prop.saveCount}"/></td>
                <td><c:out value="${prop.inquiryCount}"/></td>
                <td>
                  <span class="fw-bold" style="color:var(--re-secondary)">
                    <c:out value="${prop.popularityScore}"/>
                  </span>
                </td>
                <td>
                  <a href="<%= ctx %>/property?id=${prop.propertyId}"
                     target="_blank"
                     class="btn btn-sm btn-outline-brand"
                     style="padding:3px 10px;font-size:0.78rem">
                    View
                  </a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>

  </main>
</div><script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
<script>
// Platform Overview Bar Chart
var platCtx = document.getElementById('platformChart').getContext('2d');
new Chart(platCtx, {
  type: 'bar',
  data: {
    labels: ['Students','Staff','Landlords','Agents',
             'Total Listings','Active','Pending','Verified'],
    datasets: [{
      label: 'Count',
      data: [
        Number('<c:out value="${totalStudents}"/>'), 
        Number('<c:out value="${totalStaff}"/>'), 
        Number('<c:out value="${totalLandlords}"/>'),
        Number('<c:out value="${totalAgents}"/>'), 
        Number('<c:out value="${totalListings}"/>'), 
        Number('<c:out value="${activeListings}"/>'),
        Number('<c:out value="${pendingListings}"/>'), 
        Number('<c:out value="${verifiedListings}"/>')
      ],
      backgroundColor: [
        'rgba(26,60,94,0.75)','rgba(102,16,242,0.75)',
        'rgba(232,160,32,0.75)','rgba(32,201,151,0.75)',
        'rgba(13,110,253,0.75)','rgba(25,135,84,0.75)',
        'rgba(253,126,20,0.75)','rgba(13,202,240,0.75)'
      ],
      borderRadius: 6,
      borderSkipped: false
    }]
  },
  options: {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
  }
});

// User Role Doughnut Chart
var roleCtx = document.getElementById('userRoleChart').getContext('2d');
new Chart(roleCtx, {
  type: 'doughnut',
  data: {
    labels: ['Students','Staff','Landlords','Agents'],
    datasets: [{
      data: [
        Number('<c:out value="${totalStudents}"/>'), 
        Number('<c:out value="${totalStaff}"/>'),
        Number('<c:out value="${totalLandlords}"/>'), 
        Number('<c:out value="${totalAgents}"/>')
      ],
      backgroundColor: ['#1a3c5e','#6610f2','#e8a020','#20c997'],
      borderWidth: 2,
      borderColor: '#fff'
    }]
  },
  options: {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom',
        labels: { font: { size: 12 }, padding: 12 }
      }
    },
    cutout: '65%'
  }
});

// Mobile sidebar toggle
document.getElementById('sidebarToggle') &&
  document.getElementById('sidebarToggle')
    .addEventListener('click', function() {
      document.getElementById('dashSidebar').classList.toggle('open');
      document.getElementById('sidebarOverlay').classList.toggle('show');
    });
</script>
</body>
</html>