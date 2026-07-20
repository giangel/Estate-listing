<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctx = request.getContextPath();
    boolean loggedIn = (session.getAttribute("loggedInUser") != null);
%>
<div class="property-card">

    <!-- Image Wrapper -->
    <div class="card-img-wrapper">
        <a href="<%= ctx %>/property?id=${prop.propertyId}">
            <img src="${not empty prop.coverImage
                        ? pageContext.request.contextPath.concat('/').concat(prop.coverImage)
                        : pageContext.request.contextPath.concat('/assets/images/placeholder.jpg')}"
                 alt="<c:out value='${prop.title}'/>"
                 loading="lazy">
        </a>

        <!-- Overlay Badges -->
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
            <c:if test="${not prop.featured and not prop.verified
                          and prop.createdAt != null}">
                <%-- Show "New" badge if created within last 7 days --%>
                <c:set var="now"      value="<%= new java.util.Date().getTime() %>"/>
                <c:set var="created"  value="${prop.createdAt.time}"/>
                <c:if test="${(now - created) < 604800000}">
                    <span class="img-badge img-badge-new">
                        <i class="bi bi-lightning-fill"></i> New
                    </span>
                </c:if>
            </c:if>
        </div>

        <!-- Action Buttons -->
        <div class="card-actions">
            <% if (loggedIn) { %>
            <button class="card-action-btn"
                    onclick="toggleWishlist(this, ${prop.propertyId})"
                    title="Save to wishlist"
                    aria-label="Save to wishlist">
                <i class="bi bi-heart"></i>
            </button>
            <% } %>
            <button class="card-action-btn"
                    onclick="CompareManager.add(
                        ${prop.propertyId},
                        '<c:out value="${prop.title}"/>')"
                    title="Add to comparison"
                    aria-label="Compare property">
                <i class="bi bi-bar-chart-steps"></i>
            </button>
        </div>
    </div><!-- /card-img-wrapper -->

    <!-- Card Body -->
    <div class="card-body">

        <!-- Location -->
        <div class="card-location">
            <i class="bi bi-geo-alt-fill"></i>
            <c:choose>
                <c:when test="${not empty prop.location}">
                    <c:out value="${prop.location.areaName}"/>
                </c:when>
                <c:otherwise>Eruwa, Oyo State</c:otherwise>
            </c:choose>
        </div>

        <!-- Title -->
        <a href="<%= ctx %>/property?id=${prop.propertyId}"
           class="card-title-link">
            <c:out value="${prop.title}"/>
        </a>

        <!-- Specs Row -->
        <div class="card-specs">
            <div class="spec-item">
                <i class="bi bi-door-open"></i>
                ${prop.bedrooms}
                Bed${prop.bedrooms != 1 ? 's' : ''}
            </div>
            <div class="spec-item">
                <i class="bi bi-droplet-fill"></i>
                <c:choose>
                    <c:when test="${prop.waterAvailability == 'EXCELLENT'}">
                        Excellent Water
                    </c:when>
                    <c:when test="${prop.waterAvailability == 'GOOD'}">
                        Good Water
                    </c:when>
                    <c:when test="${prop.waterAvailability == 'FAIR'}">
                        Fair Water
                    </c:when>
                    <c:when test="${prop.waterAvailability == 'POOR'}">
                        Poor Water
                    </c:when>
                    <c:otherwise>Water N/A</c:otherwise>
                </c:choose>
            </div>
            <div class="spec-item">
                <i class="${prop.campusDistanceIcon}"></i>
                <c:out value="${prop.campusDistanceLabel}"/>
            </div>
        </div>

        <!-- Amenity Pills (top 3) -->
        <c:if test="${not empty prop.amenities}">
            <div class="d-flex flex-wrap gap-1 mb-2">
                <c:forEach var="amenity" items="${prop.amenities}" end="2">
                    <span style="font-size:0.7rem;background:rgba(26,60,94,0.07);
                                 color:var(--re-primary);padding:2px 8px;
                                 border-radius:50px;border:1px solid rgba(26,60,94,0.15)">
                        <i class="${amenity.amenityIcon}"></i>
                        <c:out value="${amenity.amenityName}"/>
                    </span>
                </c:forEach>
                <c:if test="${prop.amenities.size() > 3}">
                    <span style="font-size:0.7rem;color:var(--re-gray-500);
                                 padding:2px 8px">
                        +${prop.amenities.size() - 3} more
                    </span>
                </c:if>
            </div>
        </c:if>

        <!-- Card Footer -->
        <div class="card-footer-custom">
            <div class="re-price">
                <c:out value="${prop.formattedPrice}"/>
                <span>/yr</span>
            </div>
            <c:choose>
                <c:when test="${prop.totalRatings > 0}">
                    <div class="star-rating">
                        <div class="stars">
                            <c:forEach begin="1" end="5" var="star">
                                <i class="bi ${star <= prop.averageRating
                                               ? 'bi-star-fill'
                                               : (star - 0.5 <= prop.averageRating
                                                  ? 'bi-star-half'
                                                  : 'bi-star')}"
                                   style="color:#f5a623;font-size:0.75rem"></i>
                            </c:forEach>
                        </div>
                        <span class="rating-count">(${prop.totalRatings})</span>
                    </div>
                </c:when>
                <c:otherwise>
                    <span style="font-size:0.75rem;color:var(--re-gray-500)">
                        No ratings yet
                    </span>
                </c:otherwise>
            </c:choose>
        </div>

    </div><!-- /card-body -->
</div>