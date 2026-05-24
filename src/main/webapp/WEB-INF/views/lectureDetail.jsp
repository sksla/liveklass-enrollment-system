<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${ pageContext.request.contextPath }" />
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>강의 관리</title>

    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Bootstrap -->
    <link
      rel="stylesheet"
      href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
    />

    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>

    <style>
      body {
        background-color: #f5f6f8;
      }

      .wrap {
        width: 1200px;
        margin: auto;
        padding: 40px 0px;
      }

      .page-title {
        margin-bottom: 30px;
      }

      .card-area {
        background: white;
        padding: 25px;
        border-radius: 10px;
        margin-bottom: 25px;
      }

      .status-badge {
        padding: 5px 10px;
        border-radius: 5px;
        color: white;
        font-size: 13px;
      }

      .draft {
        background: gray;
      }

      .open {
        background: #28a745;
      }

      .closed {
        background: #dc3545;
      }

      .table td,
      .table th {
        vertical-align: middle;
      }

      textarea {
        resize: none;
      }

      .table-scroll {
        max-height: 350px;
        overflow-y: auto;
      }
    </style>
  </head>

  <body>
    <div class="wrap">
      <!-- 제목 -->
      <div class="page-title d-flex justify-content-between align-items-center">
        <h2>강의 상세 조회</h2>

        <a href="${ contextPath }" class="btn btn-outline-dark"> 메인으로 </a>
      </div>

      <!-- 강의 상세조회 -->
      <div class="card-area">
        <h4 class="mb-4">강의 상세조회</h4>

        <form
          id="modifyForm"
          method="post"
          action="${ contextPath }/lecture/modify.do"
        >
          <input type="hidden" name="lecId" value="${ lecture.lecId }"/>
          <div class="form-group">
            <label>강의 제목</label>

            <input
              type="text"
              class="form-control"
              value="${ lecture.lecTitle }"
              disabled
            />
          </div>

          <div class="form-group">
            <label>강의 설명</label>

            <textarea class="form-control" rows="4" disabled>${ lecture.description }</textarea>
          </div>

          <div class="form-row">
            <div class="form-group col-md-4">
              <label>가격</label>

              <input
                type="text"
                class="form-control"
                value="${ lecture.price }원"
                disabled
              />
            </div>

            <div class="form-group col-md-4">
              <label>최대 정원</label>

              <input
                type="text"
                class="form-control"
                value="${ lecture.capacity }명"
                disabled
              />
            </div>

            <div class="form-group col-md-4">

              <label>강의 상태</label>

              <!-- 초안 -->
              <c:if test="${ lecture.status eq 'DRAFT' }">

                <select class="form-control" name="status">

                  <option value="DRAFT" selected>
                    초안
                  </option>

                  <option value="OPEN">
                    모집중
                  </option>

                </select>

              </c:if>

              <!-- 모집중 -->
              <c:if test="${ lecture.status eq 'OPEN' }">

                <select class="form-control" name="status">

                  <option value="OPEN" selected>
                    모집중
                  </option>

                  <option value="CLOSED">
                    모집마감
                  </option>

                </select>

              </c:if>

              <!-- 모집마감 -->
              <c:if test="${ lecture.status eq 'CLOSED' }">

                <input
                  type="text"
                  class="form-control"
                  value="모집마감"
                  disabled
                />

              </c:if>

            </div>
          </div>

          <div class="form-row">
            <div class="form-group col-md-6">
              <label>수강 시작일</label>

              <input
                type="date"
                class="form-control"
                value="${ lecture.startDate }"
                disabled
              />
            </div>

            <div class="form-group col-md-6">
              <label>수강 종료일</label>

              <input
                type="date"
                class="form-control"
                value="${ lecture.endDate }"
                disabled
              />
            </div>
          </div>

          <button
            type="submit"
            class="btn btn-primary"
            ${ lecture.status eq 'CLOSED' ? 'disabled' : '' }>

            강의 수정

          </button>
        </form>
      </div>

      <!-- 수강생 목록 -->
      <div class="card-area">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h4>수강생 목록</h4>

          <span>
            현재 신청 인원 :
            <strong>
              ${ lecture.currentEnrollmentCount } / ${ lecture.capacity }
            </strong>
          </span>

        </div>

        <!-- 스크롤 영역 -->
        <div class="table-scroll">

          <table class="table">

            <thead class="thead-light">
              <tr>
                <th width="50%">이름</th>
                <th width="50%">신청일</th>
              </tr>
            </thead>

            <tbody>

              <c:choose>

                <c:when test="${ empty list }">

                  <tr>
                    <td colspan="2">
                      수강생이 없습니다.
                    </td>
                  </tr>

                </c:when>

                <c:otherwise>

                  <c:forEach var="m" items="${ list }">

                    <tr>

                      <td>
                        ${ m.memName }
                      </td>

                      <td>
                        ${ m.confirmedAt }
                      </td>

                    </tr>

                  </c:forEach>

                </c:otherwise>

              </c:choose>

            </tbody>

          </table>

        </div>
      </div>
    </div>
  </body>
</html>
