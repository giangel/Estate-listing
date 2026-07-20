<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx     = request.getContextPath();
    int    ownerId = (Integer) session.getAttribute("userId");
    com.realestate.dao.PropertyDAO pDAO =
        new com.realestate.dao.PropertyDAO();
    java.util.List<com.realestate.model.Property> props =
        pDAO.findByOwner(ownerId);
    request.setAttribute("ownerProperties", props);

    // Totals
    int totalViews     = 0;
    int totalSaves     = 0;
    int totalInquiries = 0;
    for (com.realestate.model.Property p : props) {
        totalViews     += p.getViewCount();
        totalSaves     += p.getSaveCount();
        totalInquiries += p.getInquiryCount();
    }
    request.setAttribute("totalViews",     totalViews);
    request.setAttribute("totalSaves",     totalSaves);
    request.setAttribute("totalInquiries", totalInquiries);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-4">

  <div class="dashboard-page-header">
    <div>
      <h2><i class="bi bi-graph-up me-2"></i>Property Performance</h2>
      <p class="text-muted mb-0">
        Analytics across all your listings.
      </p>
    </div>
  </div>

  <!-- Summary Stats -->
  <div class="row g-3 mb-4">
    <div class="col-md-3">
      <div class="stat-card" style="--stat-color:#1a3c5e">
        <div class="stat-icon"><i class="bi bi-building"></i></div>
        <div class="stat-info">
          <div class="stat-num">${ownerProperties.size()}</div>
          <div class="stat-label">Total Listings</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card" style="--stat-color:#0d6efd">
        <div class="stat-icon" style="background:rgba(13,110,253,0.1)">
          <i class="bi bi-eye" style="color:#0d6efd"></i>
        </div>
        <div class="stat-info">
          <div class="stat-num">${totalViews}</div>
          <div class="stat-label">Total Views</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card" style="--stat-color:#dc3545">
        <div class="stat-icon" style="background:rgba(220,53,69,0.1)">
          <i class="bi bi-heart" style="color:#dc3545"></i>
        </div>
        <div class="stat-info">
          <div class="stat-num">${totalSaves}</div>
          <div class="stat-label">Total Saves</div>
        </div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card" style="--stat-color:#198754">
        <div class="stat-icon" style="background:rgba(25,135,84,0.1)">
          <i class="bi bi-envelope" style="color:#198754"></i>
        </div>
        <div class="stat-info">
          <div class="stat-num">${totalInquiries}</div>
          <div class="stat-label">Total Inquiries</div>
        </div>
      </div>
    </div>
  </div>

  <!-- Per-Property Performance Table -->
  <div class="re-table-wrapper">
    <div class="re-table-header">
      <h5>Performance Per Listing</h5>
    </div>
    <div class="table-responsive">
      <table class="re-table">
        <thead>
          <tr>
            <th>Property</th>
            <th>Status</th>
            <th>Views</th>
            <th>Saves</th>
            <th>Inquiries</th>
            <th>Score</th>
            <th>Listed</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="prop" items="${ownerProperties}">
            <tr>
              <td>
                <a href="<%= ctx %>/property?id=${prop.propertyId}"
                   target="_blank"
                   style="font-weight:600;font-size:0.875rem;
                          color:var(--re-primary)">
                  <c:out value="${prop.title}"/>
                </a>
                <div style="font-size:0.75rem;color:var(--re-gray-500)">
                  <c:out value="${prop.formattedPrice}"/> / yr
                </div>
              </td>
              <td>
                <span class="re-badge ${prop.statusBadgeClass}"
                      style="font-size:0.72rem">
                  <c:out value="${prop.propertyStatus}"/>
                </span>
              </td>
              <td>
                <div class="d-flex align-items-center gap-1">
                  <i class="bi bi-eye text-muted"
                     style="font-size:0.85rem"></i>
                  ${prop.viewCount}
                </div>
              </td>
              <td>
                <div class="d-flex align-items-center gap-1">
                  <i class="bi bi-heart text-danger"
                     style="font-size:0.85rem"></i>
                  ${prop.saveCount}
                </div>
              </td>
              <td>
                <div class="d-flex align-items-center gap-1">
                  <i class="bi bi-envelope text-success"
                     style="font-size:0.85rem"></i>
                  ${prop.inquiryCount}
                </div>
              </td>
              <td>
                <strong style="color:var(--re-secondary)">
                  ${prop.popularityScore}
                </strong>
              </td>
              <td style="font-size:0.78rem;color:var(--re-gray-500)">
                <fmt:formatDate value="${prop.createdAt}"
                                pattern="dd MMM yyyy"/>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>

</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>