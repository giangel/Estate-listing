<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx     = request.getContextPath();
    int    ownerId = (Integer) session.getAttribute("userId");

    // Load properties owned by this user
    com.realestate.dao.PropertyDAO pdao =
        new com.realestate.dao.PropertyDAO();
    java.util.List<com.realestate.model.Property> myProperties =
        pdao.findByOwner(ownerId);
    request.setAttribute("myProperties", myProperties);
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
      <h2><i class="bi bi-building me-2"></i>My Properties</h2>
      <p class="text-muted mb-0">
        Manage your property listings below.
      </p>
    </div>
    <a href="<%= ctx %>/landlord/create-property"
       class="btn btn-primary-brand btn-sm-custom">
      <i class="bi bi-plus-circle me-1"></i> Add New Property
    </a>
  </div>

  <%@ include file="/includes/alerts.jsp" %>

  <div class="re-table-wrapper">
    <div class="table-responsive">
      <table class="re-table">
        <thead>
          <tr>
            <th style="width:60px">Cover</th>
            <th>Title</th>
            <th>Type</th>
            <th>Price</th>
            <th>Distance</th>
            <th>Status</th>
            <th>Views</th>
            <th>Inquiries</th>
            <th>Listed</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${not empty myProperties}">
              <c:forEach var="prop" items="${myProperties}">
                <tr>
                  <td>
                    <img src="<%= ctx %>${not empty prop.coverImage
            ? '/'.concat(prop.coverImage)
            : '/assets/images/placeholder.jpg'}"
                         alt="Cover"
                         style="width:50px;height:40px;object-fit:cover;
                                border-radius:6px">
                  </td>
                  <td>
                    <a href="<%= ctx %>/property?id=${prop.propertyId}"
                       target="_blank"
                       style="font-weight:600;font-size:0.875rem">
                      <c:out value="${prop.title}"/>
                    </a>
                    <c:if test="${prop.verified}">
                      <span class="re-badge re-badge-verified ms-1"
                            style="font-size:0.65rem">
                        ✓ Verified
                      </span>
                    </c:if>
                  </td>
                  <td style="font-size:0.82rem">
                    <c:out value="${prop.typeName}"/>
                  </td>
                  <td class="fw-bold"
                      style="color:var(--re-secondary);font-size:0.875rem">
                    <c:out value="${prop.formattedPrice}"/>
                  </td>
                  <td>
                    <span class="re-badge re-badge-distance"
                          style="font-size:0.72rem">
                      <c:out value="${prop.campusDistanceLabel}"/>
                    </span>
                  </td>
                  <td>
                    <span class="re-badge ${prop.statusBadgeClass}"
                          style="font-size:0.72rem">
                      <c:out value="${prop.propertyStatus}"/>
                    </span>
                  </td>
                  <td style="font-size:0.82rem;text-align:center">
                    <i class="bi bi-eye text-muted me-1"></i>
                    ${prop.viewCount}
                  </td>
                  <td style="font-size:0.82rem;text-align:center">
                    <i class="bi bi-envelope text-muted me-1"></i>
                    ${prop.inquiryCount}
                  </td>
                  <td style="font-size:0.78rem;color:var(--re-gray-500)">
                    <fmt:formatDate value="${prop.createdAt}"
                                    pattern="dd/MM/yy"/>
                  </td>
                  <td>
                    <div class="d-flex gap-1">
                      <a href="<%= ctx %>/landlord/update-property?id=${prop.propertyId}"
                         class="btn btn-sm btn-outline-brand"
                         style="padding:4px 10px;font-size:0.78rem"
                         title="Edit">
                        <i class="bi bi-pencil"></i>
                      </a>
                      <form action="<%= ctx %>/landlord/delete-property"
                            method="POST" style="display:inline"
                            onsubmit="return confirm(
                              'Delete \'${prop.title}\'? This cannot be undone.')">
                        <input type="hidden" name="_csrf"
                               value="${sessionScope.csrfToken}">
                        <input type="hidden" name="propertyId"
                               value="${prop.propertyId}">
                        <button type="submit"
                                class="btn btn-sm"
                                style="background:#dc3545;color:white;
                                       padding:4px 10px;font-size:0.78rem"
                                title="Delete">
                          <i class="bi bi-trash"></i>
                        </button>
                      </form>
                      <a href="<%= ctx %>/property?id=${prop.propertyId}"
                         class="btn btn-sm btn-outline-secondary"
                         style="padding:4px 10px;font-size:0.78rem"
                         target="_blank" title="Preview">
                        <i class="bi bi-eye"></i>
                      </a>
                    </div>
                  </td>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr>
                <td colspan="10">
                  <div class="empty-state py-4">
                    <div class="empty-icon" style="font-size:3rem">
                      <i class="bi bi-building-slash"></i>
                    </div>
                    <h5 class="mt-3">No Properties Listed Yet</h5>
                    <p class="text-muted">
                      Start by adding your first property listing.
                    </p>
                    <a href="<%= ctx %>/landlord/create-property"
                       class="btn btn-primary-brand">
                      <i class="bi bi-plus-circle me-1"></i>
                      Add Property
                    </a>
                  </div>
                </td>
              </tr>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>

</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
</body>
</html>