<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% 
    String ctx = request.getContextPath(); 
    
    // Emergency Role Override Filter Patch
    com.realestate.model.User loggedInUser = (com.realestate.model.User) session.getAttribute("loggedInUser");
    if (loggedInUser != null && "STUDENT".equals(loggedInUser.getRoleName())) {
        // Bridge session role status parameter so backend routes authorize cleanly
        session.setAttribute("userRole", "USER"); 
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <jsp:include page="/includes/head-meta.jsp" />
</head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container-fluid py-4 px-4">

  <nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb" style="font-size:0.82rem">
      <li class="breadcrumb-item"><a href="<%= ctx %>/index.jsp">Home</a></li>
      <li class="breadcrumb-item active">Student Dashboard</li>
    </ol>
  </nav>

  <%@ include file="/includes/alerts.jsp" %>

  <div class="row g-3 mb-4">
    <div class="col-md-3 col-6">
      <a href="<%= ctx %>/user/wishlist"
         class="d-block p-3 text-center rounded-3 text-decoration-none"
         style="background:rgba(220,53,69,0.08); border:1px solid rgba(220,53,69,0.2)">
        <i class="bi bi-heart-fill" style="font-size:1.8rem;color:#dc3545"></i>
        <div class="fw-bold mt-1" style="font-size:1.3rem;color:#dc3545">
          <c:out value="${not empty savedCount ? savedCount : '0'}"/>
        </div>
        <div style="font-size:0.8rem;color:var(--re-gray-500)">Saved</div>
      </a>
    </div>
    <div class="col-md-3 col-6">
      <a href="<%= ctx %>/user/inquiry"
         class="d-block p-3 text-center rounded-3 text-decoration-none"
         style="background:rgba(13,110,253,0.08); border:1px solid rgba(13,110,253,0.2)">
        <i class="bi bi-envelope-fill" style="font-size:1.8rem;color:#0d6efd"></i>
        <div class="fw-bold mt-1" style="font-size:1.3rem;color:#0d6efd">
          <c:out value="${not empty inquiryCount ? inquiryCount : '0'}"/>
        </div>
        <div style="font-size:0.8rem;color:var(--re-gray-500)">Inquiries</div>
      </a>
    </div>
    <div class="col-md-3 col-6">
      <a href="<%= ctx %>/user/appointment"
         class="d-block p-3 text-center rounded-3 text-decoration-none"
         style="background:rgba(25,135,84,0.08); border:1px solid rgba(25,135,84,0.2)">
        <i class="bi bi-calendar-check-fill" style="font-size:1.8rem;color:#198754"></i>
        <div class="fw-bold mt-1" style="font-size:1.3rem;color:#198754">
          <c:out value="${not empty appointmentCount ? appointmentCount : '0'}"/>
        </div>
        <div style="font-size:0.8rem;color:var(--re-gray-500)">Viewings</div>
      </a>
    </div>
    <div class="col-md-3 col-6">
      <a href="<%= ctx %>/user/recently-viewed"
         class="d-block p-3 text-center rounded-3 text-decoration-none"
         style="background:rgba(232,160,32,0.08); border:1px solid rgba(232,160,32,0.2)">
        <i class="bi bi-bell-fill" style="font-size:1.8rem;color:#e8a020"></i>
        <div class="fw-bold mt-1" style="font-size:1.3rem;color:#e8a020">
          <c:out value="${not empty unreadNotifications ? unreadNotifications : '0'}"/>
        </div>
        <div style="font-size:0.8rem;color:var(--re-gray-500)">Notifications</div>
      </a>
    </div>
  </div>

  <div class="row g-4">
    <div class="col-xl-3 col-lg-3">
      <div class="filter-sidebar">
        <div class="filter-title">
          <i class="bi bi-funnel-fill"></i> Filter Accommodations
        </div>

        <form action="<%= ctx %>/properties" method="GET" id="filterForm">
        
          <div class="filter-group">
            <div class="filter-group-title">Search</div>
            <div style="position:relative">
              <i class="bi bi-search"
                 style="position:absolute;left:12px;top:50%;
                        transform:translateY(-50%);color:var(--re-gray-500);
                        font-size:0.85rem"></i>
              <input type="text" class="re-form-control" name="keyword"
                     style="padding-left:2.2rem;height:40px;font-size:0.85rem"
                     placeholder="Search properties..."
                     value="<c:out value='${keyword}'/>">
            </div>
          </div>

          <div class="filter-group">
            <div class="filter-group-title">Property Type</div>
            <c:forEach var="type" items="${propertyTypes}">
              <div class="filter-checkbox">
                <input type="radio" name="type" id="type_${type.typeId}"
                       value="<c:out value='${type.typeId}'/>"
                       <c:if test="${selectedType == type.typeId}">checked</c:if>>
                <label for="type_${type.typeId}">
                  <c:out value="${type.typeName}"/>
                </label>
              </div>
            </c:forEach>
            <div class="filter-checkbox">
              <input type="radio" name="type" id="typeAll" value=""
                     <c:if test="${selectedType == 0}">checked</c:if>>
              <label for="typeAll">All Types</label>
            </div>
          </div>

          <div class="filter-group">
            <div class="filter-group-title">Distance from Campus</div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="distAll" value=""
                     <c:if test="${empty selectedDist}">checked</c:if>>
              <label for="distAll">Any Distance</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="distOnCampus" value="ON_CAMPUS"
                     <c:if test="${selectedDist == 'ON_CAMPUS'}">checked</c:if>>
              <label for="distOnCampus">🏫 On Campus</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist5" value="LESS_5MIN"
                     <c:if test="${selectedDist == 'LESS_5MIN'}">checked</c:if>>
              <label for="dist5">🚶 Less than 5 min</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist5to10" value="5_TO_10MIN"
                     <c:if test="${selectedDist == '5_TO_10MIN'}">checked</c:if>>
              <label for="dist5to10">🚶 5 – 10 min</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist10to15" value="10_TO_15MIN"
                     <c:if test="${selectedDist == '10_TO_15MIN'}">checked</c:if>>
              <label for="dist10to15">🚲 10 – 15 min</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist15" value="ABOVE_15MIN"
                     <c:if test="${selectedDist == 'ABOVE_15MIN'}">checked</c:if>>
              <label for="dist15">🚗 Above 15 min</label>
            </div>
          </div>

          <div class="filter-group">
            <div class="filter-group-title">Budget (₦ / Year)</div>
            <div class="row g-2">
              <div class="col-6">
                <input type="number" class="re-form-control"
                       name="minPrice" placeholder="Min"
                       style="height:38px;font-size:0.82rem"
                       value="<c:out value='${minPrice}'/>">
              </div>
              <div class="col-6">
                <input type="number" class="re-form-control"
                       name="maxPrice" placeholder="Max"
                       style="height:38px;font-size:0.82rem"
                       value="<c:out value='${maxPrice}'/>">
              </div>
            </div>
            <div class="mt-2 d-flex flex-wrap gap-1">
              <button type="button" class="btn btn-sm border rounded-pill" style="font-size:0.72rem;padding:2px 8px" onclick="setBudget(0,50000)">&lt;₦50k</button>
              <button type="button" class="btn btn-sm border rounded-pill" style="font-size:0.72rem;padding:2px 8px" onclick="setBudget(50000,100000)">₦50k–100k</button>
              <button type="button" class="btn btn-sm border rounded-pill" style="font-size:0.72rem;padding:2px 8px" onclick="setBudget(100000,150000)">₦100k–150k</button>
              <button type="button" class="btn btn-sm border rounded-pill" style="font-size:0.72rem;padding:2px 8px" onclick="setBudget(150000,200000)">₦150k–200k</button>
            </div>
          </div>

          <div class="d-grid gap-2">
            <button type="submit" class="btn btn-primary-brand"><i class="bi bi-funnel me-1"></i> Apply Filters</button>
            <a href="<%= ctx %>/properties" class="btn btn-outline-secondary btn-sm"><i class="bi bi-x-circle me-1"></i> Clear All</a>
          </div>
        </form>
      </div>
    </div>

    <div class="col-xl-9 col-lg-9">
      <div class="results-toolbar">
        <div class="results-count">
          <strong><c:out value="${totalResults}"/></strong> saved allocations tracked
        </div>

        <div class="d-flex align-items-center gap-3">
          <select class="re-form-control" style="width:auto;height:36px;font-size:0.82rem" onchange="sortResults(this.value)">
            <option value="date" <c:if test="${sortBy == 'date' || empty sortBy}">selected</c:if>>Newest First</option>
            <option value="price_asc" <c:if test="${sortBy == 'price_asc'}">selected</c:if>>Price: Low to High</option>
            <option value="price_desc" <c:if test="${sortBy == 'price_desc'}">selected</c:if>>Price: High to Low</option>
          </select>

          <div class="view-toggle">
            <button class="view-btn active" id="gridViewBtn" onclick="setView('grid')"><i class="bi bi-grid-3x3-gap"></i></button>
            <button class="view-btn" id="listViewBtn" onclick="setView('list')"><i class="bi bi-list-ul"></i></button>
          </div>
        </div>
      </div>

      <div class="row g-4" id="propertyGrid">
        <c:choose>
          <c:when test="${not empty properties}">
            <c:forEach var="prop" items="${properties}">
              <div class="col-xl-4 col-md-6 col-12">
                <div class="property-card">
                  <div class="card-img-wrapper">
                    <a href="<%= ctx %>/property?id=<c:out value='${prop.propertyId}'/>">
<img src="<%= ctx %><c:out value='${not empty prop.coverImage ? "/".concat(prop.coverImage) : "/assets/images/placeholder.jpg"}'/>"
     alt="<c:out value='${prop.title}'/>">
                         </a>
                  </div>
                  <div class="card-body">
                    <div class="card-location"><i class="bi bi-geo-alt-fill"></i> <c:out value="${prop.location.areaName}"/></div>
                    <a href="<%= ctx %>/property?id=<c:out value='${prop.propertyId}'/>" class="card-title-link"><c:out value="${prop.title}"/></a>
                    <div class="card-footer-custom">
                      <div class="re-price"><c:out value="${prop.formattedPrice}"/><span>/yr</span></div>
                    </div>
                  </div>
                </div>
              </div>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <div class="col-12">
              <div class="empty-state">
                <div class="empty-icon"><i class="bi bi-building-slash"></i></div>
                <h4>No Selections Active</h4>
                <p>Your current dashboard criteria returned no matching logs. Use the search parameters to populate entries.</p>
              </div>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/includes/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
<script>
function sortResults(val) {
  var url = new URL(window.location.href);
  url.searchParams.set('sort', val);
  window.location = url.toString();
}
function setBudget(min, max) {
  document.querySelector('[name="minPrice"]').value = min || '';
  document.querySelector('[name="maxPrice"]').value = max || '';
}
function setView(mode) {
  var grid = document.getElementById('propertyGrid');
  var gb   = document.getElementById('gridViewBtn');
  var lb   = document.getElementById('listViewBtn');
  if (mode === 'list') {
    grid.querySelectorAll('.col-xl-4').forEach(function(c) { c.className = c.className.replace('col-xl-4 col-md-6', 'col-12'); });
    lb.classList.add('active'); gb.classList.remove('active');
  } else {
    grid.querySelectorAll('.col-12').forEach(function(c) { if (c.querySelector('.property-card')) { c.className = c.className.replace('col-12', 'col-xl-4 col-md-6 col-12'); } });
    gb.classList.add('active'); lb.classList.remove('active');
  }
}
</script>
</body>
</html>