<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
String ctx = request.getContextPath();
// Load properties for admin view
com.realestate.dao.PropertyDAO pdao = new com.realestate.dao.PropertyDAO();
String statusFilter = request.getParameter("status");
java.util.List<com.realestate.model.Property> allProperties;
if ("PENDING".equals(statusFilter)) {
	allProperties = pdao.findPending();
} else {
	allProperties = pdao.searchProperties(null, 0, 0, null, null, null, "date", 1, 100);
}
request.setAttribute("allProperties", allProperties);
request.setAttribute("statusFilter", statusFilter);
request.setAttribute("pendingCount", pdao.countPending());
%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@ include file="/includes/head-meta.jsp"%>
<link rel="stylesheet" href="<%=ctx%>/assets/css/dashboard.css">
</head>
<body data-ctx="<%=ctx%>">
	<%@ include file="/includes/navbar.jsp"%>

	<div class="re-dashboard">
		<jsp:include page="/includes/admin-sidebar.jsp" />
		<main class="re-dashboard-content">

			<div class="dashboard-page-header">
				<div>
					<h2>
						<i class="bi bi-building me-2"></i>Property Management
					</h2>
				</div>
				<div class="d-flex gap-2">
					<a href="<%= ctx %>/admin/manage-properties"
						class="btn btn-sm ${empty statusFilter ? 'btn-primary-brand' : 'btn-outline-secondary'}">
						All </a> <a href="<%= ctx %>/admin/manage-properties?status=PENDING"
						class="btn btn-sm ${statusFilter == 'PENDING' ? 'btn-secondary-brand' : 'btn-outline-secondary'}">
						Pending (${pendingCount}) </a>
				</div>
			</div>

			<%@ include file="/includes/alerts.jsp"%>

			<div class="re-table-wrapper">
				<div class="table-responsive">
					<table class="re-table">
						<thead>
							<tr>
								<th>Cover</th>
								<th>Title</th>
								<th>Owner</th>
								<th>Type</th>
								<th>Price</th>
								<th>Status</th>
								<th>Views</th>
								<th>Date</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="prop" items="${allProperties}">
								<tr>
									<td><img
										src="<%= ctx %>${not empty prop.coverImage
            ? '/'.concat(prop.coverImage)
            : '/assets/images/placeholder.jpg'}"
										style="width: 50px; height: 38px; object-fit: cover; border-radius: 6px">
									</td>
									<td style="font-size: 0.875rem"><a
										href="<%= ctx %>/property?id=${prop.propertyId}"
										target="_blank" style="font-weight: 600"> <c:out
												value="${prop.title}" />
									</a> <c:if test="${prop.verified}">
											<span class="re-badge re-badge-verified ms-1"
												style="font-size: 0.65rem">✓</span>
										</c:if></td>
									<td style="font-size: 0.82rem"><c:out
											value="${prop.ownerName}" /></td>
									<td style="font-size: 0.8rem"><c:out
											value="${prop.typeName}" /></td>
									<td
										style="font-weight: 700; color: var(--re-secondary); font-size: 0.875rem">
										<c:out value="${prop.formattedPrice}" />
									</td>
									<td><span class="re-badge ${prop.statusBadgeClass}"
										style="font-size: 0.72rem"> <c:out
												value="${prop.propertyStatus}" />
									</span></td>
									<td style="text-align: center; font-size: 0.82rem">
										${prop.viewCount}</td>
									<td style="font-size: 0.78rem; color: var(--re-gray-500)">
										<fmt:formatDate value="${prop.createdAt}" pattern="dd/MM/yy" />
									</td>
									<td>
										<div class="d-flex gap-1 flex-wrap">
											<c:if test="${prop.propertyStatus == 'PENDING'}">
												<form action="<%=ctx%>/admin/approve-property"
													method="POST" style="display: inline">
													<input type="hidden" name="_csrf"
														value="${sessionScope.csrfToken}"> <input
														type="hidden" name="propertyId" value="${prop.propertyId}">
													<input type="hidden" name="action" value="approve">
													<button type="submit" class="btn btn-sm"
														style="background: #198754; color: white; padding: 3px 8px; font-size: 0.75rem"
														onclick="return confirm('Approve listing?')">
														<i class="bi bi-check"></i>
													</button>
												</form>
												<form action="<%=ctx%>/admin/approve-property"
													method="POST" style="display: inline">
													<input type="hidden" name="_csrf"
														value="${sessionScope.csrfToken}"> <input
														type="hidden" name="propertyId" value="${prop.propertyId}">
													<input type="hidden" name="action" value="reject">
													<button type="submit" class="btn btn-sm"
														style="background: #dc3545; color: white; padding: 3px 8px; font-size: 0.75rem"
														onclick="return confirm('Reject listing?')">
														<i class="bi bi-x"></i>
													</button>
												</form>
											</c:if>
											<form action="<%=ctx%>/landlord/delete-property"
												method="POST" style="display: inline">
												<input type="hidden" name="_csrf"
													value="${sessionScope.csrfToken}"> <input
													type="hidden" name="propertyId" value="${prop.propertyId}">
												<button type="submit" class="btn btn-sm btn-outline-danger"
													style="padding: 3px 8px; font-size: 0.75rem"
													onclick="return confirm('Delete this listing?')">
													<i class="bi bi-trash"></i>
												</button>
											</form>
										</div>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>

		</main>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="<%=ctx%>/assets/js/dark-mode.js"></script>
	<script src="<%=ctx%>/assets/js/main.js"></script>
</body>
</html>