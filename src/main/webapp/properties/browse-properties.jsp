<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx = request.getContextPath(); %>
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
      <li class="breadcrumb-item active">Browse Properties</li>
    </ol>
  </nav>

  <%@ include file="/includes/alerts.jsp" %>

  <div class="row g-4">

    <div class="col-xl-3 col-lg-3">
      <div class="filter-sidebar">
        <div class="filter-title">
          <i class="bi bi-funnel-fill"></i> Filter Properties
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
            <c:set var="distances" value="ON_CAMPUS,LESS_5MIN,5_TO_10MIN,10_TO_15MIN,ABOVE_15MIN"/>
            <c:set var="distLabels" value="On Campus,Less than 5 min,5 – 10 min,10 – 15 min,Above 15 min"/>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="distAll" value=""
                     <c:if test="${empty selectedDist}">checked</c:if>>
              <label for="distAll">Any Distance</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="distOnCampus"
                     value="ON_CAMPUS"
                     <c:if test="${selectedDist == 'ON_CAMPUS'}">checked</c:if>>
              <label for="distOnCampus">🏫 On Campus</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist5"
                     value="LESS_5MIN"
                     <c:if test="${selectedDist == 'LESS_5MIN'}">checked</c:if>>
              <label for="dist5">🚶 Less than 5 min</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist5to10"
                     value="5_TO_10MIN"
                     <c:if test="${selectedDist == '5_TO_10MIN'}">checked</c:if>>
              <label for="dist5to10">🚶 5 – 10 min</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist10to15"
                     value="10_TO_15MIN"
                     <c:if test="${selectedDist == '10_TO_15MIN'}">checked</c:if>>
              <label for="dist10to15">🚲 10 – 15 min</label>
            </div>
            <div class="filter-checkbox">
              <input type="radio" name="distance" id="dist15"
                     value="ABOVE_15MIN"
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
              <button type="button"
                      class="btn btn-sm border rounded-pill"
                      style="font-size:0.72rem;padding:2px 8px"
                      onclick="setBudget(0,50000)">
                &lt;₦50k
              </button>
              <button type="button"
                      class="btn btn-sm border rounded-pill"
                      style="font-size:0.72rem;padding:2px 8px"
                      onclick="setBudget(50000,100000)">
                ₦50k–100k
              </button>
              <button type="button"
                      class="btn btn-sm border rounded-pill"
                      style="font-size:0.72rem;padding:2px 8px"
                      onclick="setBudget(100000,150000)">
                ₦100k–150k
              </button>
              <button type="button"
                      class="btn btn-sm border rounded-pill"
                      style="font-size:0.72rem;padding:2px 8px"
                      onclick="setBudget(150000,200000)">
                ₦150k–200k
              </button>
            </div>
          </div>

          <div class="d-grid gap-2">
            <button type="submit" class="btn btn-primary-brand">
              <i class="bi bi-funnel me-1"></i> Apply Filters
            </button>
            <a href="<%= ctx %>/properties"
               class="btn btn-outline-secondary btn-sm">
              <i class="bi bi-x-circle me-1"></i> Clear All
            </a>
          </div>

        </form>
      </div>
    </div>

    <div class="col-xl-9 col-lg-9">

      <div class="results-toolbar">
        <div class="results-count">
          <strong><c:out value="${totalResults}"/></strong> properties found
          <c:if test="${not empty keyword}">
            for "<c:out value='${keyword}'/>"
          </c:if>
        </div>

        <div class="d-flex align-items-center gap-3">
          <select class="re-form-control"
                  style="width:auto;height:36px;font-size:0.82rem"
                  onchange="sortResults(this.value)">
            <option value="date"
                    <c:if test="${sortBy == 'date' || empty sortBy}">selected</c:if>>
              Newest First
            </option>
            <option value="price_asc"
                    <c:if test="${sortBy == 'price_asc'}">selected</c:if>>
              Price: Low to High
            </option>
            <option value="price_desc"
                    <c:if test="${sortBy == 'price_desc'}">selected</c:if>>
              Price: High to Low
            </option>
            <option value="popular"
                    <c:if test="${sortBy == 'popular'}">selected</c:if>>
              Most Popular
            </option>
            <option value="rating"
                    <c:if test="${sortBy == 'rating'}">selected</c:if>>
              Top Rated
            </option>
          </select>

          <div class="view-toggle">
            <button class="view-btn active" id="gridViewBtn"
                    onclick="setView('grid')" title="Grid view">
              <i class="bi bi-grid-3x3-gap"></i>
            </button>
            <button class="view-btn" id="listViewBtn"
                    onclick="setView('list')" title="List view">
              <i class="bi bi-list-ul"></i>
            </button>
          </div>
        </div>
      </div>

      <div id="compareBar"
           style="background:var(--re-primary);color:white;padding:10px 16px;
                  border-radius:10px;margin-bottom:1rem;display:none;
                  align-items:center;justify-content:space-between;
                  flex-wrap:wrap;gap:0.5rem">
        <span>
          <i class="bi bi-bar-chart-steps me-2"></i>
          <span id="compareCount">0</span> properties selected for comparison
        </span>
        <div class="d-flex gap-2">
          <button onclick="CompareManager.navigateToCompare()"
                  class="btn btn-sm"
                  style="background:var(--re-secondary);color:white">
            Compare Now
          </button>
          <button onclick="CompareManager.clear()"
                  class="btn btn-sm btn-outline-light">
            Clear
          </button>
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
                           alt="<c:out value='${prop.title}'/>"
                           loading="lazy">
                    </a>

                    <div class="card-badges">
                      <c:if test="${prop.featured}">
                        <span class="img-badge img-badge-featured">
                          <i class="bi bi-star-fill"></i> Featured
                        </span>
                      </c:if>
                      <c:if test="${prop.verified}">
                        <span class="img-badge img-badge-verified">
                          <i class="bi bi-patch-check-fill"></i> Verified
                        </span>
                      </c:if>
                    </div>

                    <div class="card-actions">
                      <button class="card-action-btn"
                              onclick="toggleWishlist(this, Number('<c:out value="${prop.propertyId}"/>'))"
                              title="Save to wishlist">
                        <i class="bi bi-heart"></i>
                      </button>
                      <button class="card-action-btn"
                              onclick="CompareManager.add(
                                Number('<c:out value="${prop.propertyId}"/>'),
                                '<c:out value="${prop.title}"/>')"
                              title="Add to comparison">
                        <i class="bi bi-bar-chart-steps"></i>
                      </button>
                    </div>
                  </div>

                  <div class="card-body">
                    <div class="card-location">
                      <i class="bi bi-geo-alt-fill"></i>
                      <c:out value="${prop.location != null
                                     ? prop.location.areaName
                                     : 'Eruwa, Oyo State'}"/>
                    </div>

                    <a href="<%= ctx %>/property?id=<c:out value='${prop.propertyId}'/>"
                       class="card-title-link">
                      <c:out value="${prop.title}"/>
                    </a>

                    <div class="card-specs">
                      <div class="spec-item">
                        <i class="bi bi-door-open"></i>
                        <c:out value="${prop.bedrooms}"/> Bed<c:out value="${prop.bedrooms != 1 ? 's' : ''}"/>
                      </div>
                      <div class="spec-item">
                        <i class="bi bi-droplet"></i>
                        <c:out value="${prop.waterAvailability}"/>
                      </div>
                      <div class="spec-item">
                        <i class="<c:out value='${prop.campusDistanceIcon}'/>"></i>
                        <c:out value="${prop.campusDistanceLabel}"/>
                      </div>
                    </div>

                    <div class="card-footer-custom">
                      <div class="re-price">
                        <c:out value="${prop.formattedPrice}"/>
                        <span>/yr</span>
                      </div>
                      <c:if test="${prop.totalRatings > 0}">
                        <div class="star-rating">
                          <div class="stars">
                            <c:forEach begin="1" end="5" var="star">
                              <i class="bi <c:out value="${star <= prop.averageRating ? 'bi-star-fill' : (star - 0.5 <= prop.averageRating ? 'bi-star-half' : 'bi-star')}"/>"></i>
                            </c:forEach>
                          </div>
                          <span class="rating-count">(<c:out value="${prop.totalRatings}"/>)</span>
                        </div>
                      </c:if>
                    </div>

                  </div>
                </div>
              </div>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <div class="col-12">
              <div class="empty-state">
                <div class="empty-icon">
                  <i class="bi bi-building-slash"></i>
                </div>
                <h4>No Properties Found</h4>
                <p>
                  No properties match your current search criteria.
                  Try adjusting your filters or broadening your search.
                </p>
                <a href="<%= ctx %>/properties"
                   class="btn btn-primary-brand">
                  <i class="bi bi-arrow-repeat me-2"></i> Clear Filters
                </a>
              </div>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <c:if test="${totalPages > 1}">
        <div class="re-pagination">
          <c:if test="${currentPage > 1}">
            <a class="page-btn"
               href="<%= ctx %>/properties?page=<c:out value='${currentPage - 1}'/>&keyword=<c:out value='${keyword}'/>&type=<c:out value='${selectedType}'/>&distance=<c:out value='${selectedDist}'/>&sort=<c:out value='${sortBy}'/>">
              <i class="bi bi-chevron-left"></i>
            </a>
          </c:if>

          <c:forEach begin="1" end="${totalPages}" var="pg">
            <c:if test="${pg >= currentPage - 2 && pg <= currentPage + 2}">
              <a class="page-btn <c:out value='${pg == currentPage ? "active" : ""}'/>"
                 href="<%= ctx %>/properties?page=<c:out value='${pg}'/>&keyword=<c:out value='${keyword}'/>&type=<c:out value='${selectedType}'/>&distance=<c:out value='${selectedDist}'/>&sort=<c:out value='${sortBy}'/>">
                <c:out value="${pg}"/>
              </a>
            </c:if>
          </c:forEach>

          <c:if test="${currentPage < totalPages}">
            <a class="page-btn"
               href="<%= ctx %>/properties?page=<c:out value='${currentPage + 1}'/>&keyword=<c:out value='${keyword}'/>&type=<c:out value='${selectedType}'/>&distance=<c:out value='${selectedDist}'/>&sort=<c:out value='${sortBy}'/>">
              <i class="bi bi-chevron-right"></i>
            </a>
          </c:if>
        </div>
      </c:if>

    </div></div></div>
    <jsp:include page="/includes/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
<script src="<%= ctx %>/assets/js/compare.js"></script>
<script>
function sortResults(val) {
  var url = new URL(window.location.href);
  url.searchParams.set('sort', val);
  url.searchParams.set('page', '1');
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
    grid.querySelectorAll('.col-xl-4').forEach(function(c) {
      c.className = c.className.replace('col-xl-4 col-md-6', 'col-12');
    });
    lb.classList.add('active'); gb.classList.remove('active');
  } else {
    grid.querySelectorAll('.col-12').forEach(function(c) {
      if (c.querySelector('.property-card')) {
        c.className = c.className.replace('col-12', 'col-xl-4 col-md-6 col-12');
      }
    });
    gb.classList.add('active'); lb.classList.remove('active');
  }
}
</script>
</body>
</html>