<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
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

      .table tbody tr {
        cursor: pointer;
      }

      .table td,
      .table th {
        vertical-align: middle;
      }

      textarea {
        resize: none;
      }
    </style>
  </head>

  <body>
    <script>
      if ("${alertMsg}" !== "") {
        // 어떤 메세지 문구가 존재할 경우
        alert("${alertMsg}");

        // 이하의 코드는 alert 창을 띄운 후 추가적으로 실행할 내용
        if ("${historyBackYN}" === "Y") {
          history.back();
        }
      }
    </script>

    <div class="wrap">
      <!-- 제목 -->
      <div class="page-title d-flex justify-content-between align-items-center">
        <h2>강의 관리</h2>

        <a href="${ contextPath }" class="btn btn-outline-dark"> 메인으로 </a>
      </div>

      <!-- 강의 목록 -->
      <div class="card-area">
        <h4>강의 목록</h4>
        <br />
        <div class="d-flex justify-content-between align-items-center mb-4">
          <!-- 상태 필터 -->
          <select
            id="condition"
            class="form-control w-25"
            onchange="ajaxFilterLectureList(1)"
          >
            <option value="">전체</option>
            <option value="DRAFT">초안</option>
            <option value="OPEN">모집</option>
            <option value="CLOSED">모집마감</option>
          </select>

          <div>
            <a
              href="${ contextPath }/lecture/lecturRegistForm.page"
              class="btn btn-dark"
            >
              강의 등록
            </a>
          </div>
        </div>

        <table id="lecture-list" class="table table-hover">
          <thead class="thead-light">
            <tr>
              <th width="8%">번호</th>
              <th width="20%">강의명</th>
              <th width="12%">가격</th>
              <th width="12%">정원</th>
              <th width="15%">수강기간</th>
              <th width="11%">상태</th>
            </tr>
          </thead>

          <tbody>
            <%--
            <tr onclick="">
              <td>1</td>

              <td>Spring Boot 입문</td>

              <td>100,000원</td>

              <td>10 / 30</td>

              <td>2026-06-01 ~ 2026-07-31</td>

              <td>
                <span class="status-badge open"> 모집 중 </span>
              </td>
            </tr>

            <tr onclick="">
              <td>2</td>

              <td>MySQL 실전</td>

              <td>80,000원</td>

              <td>30 / 30</td>

              <td>2026-05-01 ~ 2026-06-30</td>

              <td>
                <span class="status-badge closed"> 모집마감 </span>
              </td>
            </tr>

            <tr onclick="">
              <td>2</td>

              <td>MySQL 기초</td>

              <td>80,000원</td>

              <td>0 / 30</td>

              <td>2026-05-01 ~ 2026-06-30</td>

              <td>
                <span class="status-badge draft"> 초안 </span>
              </td>
            </tr>
            --%>
          </tbody>
        </table>
        <br />

        <div id="pagingArea">
          <ul class="pagination justify-content-center"></ul>
        </div>
      </div>

      <script>
        $(document).ready(function () {
          ajaxFilterLectureList(1);
        });

        // ajax 강의 목록 필터링 조회용 function
        function ajaxFilterLectureList(requestPage) {
          $.ajax({
            url: "${contextPath}/lecture/filterLecture.do",
            type: "post",
            async: false,
            data: {
              page: requestPage,
              condition: $("#condition").val(),
            },
            success: function (rep) {
              let list = rep.list;
              let tbody = "";

              if (list.length > 0) {
                for (let i = 0; i < list.length; i++) {
                  let status = list[i].status;
                  let statusStr =
                    status == "OPEN"
                      ? "모집 중"
                      : status == "DRAFT"
                        ? "초안"
                        : "모집마감";

                  let statusClass =
                    status == "OPEN"
                      ? "open"
                      : status == "DRAFT"
                        ? "draft"
                        : "closed";

                  tbody +=
                    "<tr onclick='location.href=\"${contextPath}/lecture/detail.do?lecId=" +
                    list[i].lecId +
                    "\"'>" +
                    "<td>" +
                    list[i].lecId +
                    "</td>" +
                    "<td>" +
                    list[i].lecTitle +
                    "</td>" +
                    "<td>" +
                    list[i].price.toLocaleString() +
                    "원</td>" +
                    "<td>" +
                    list[i].currentEnrollmentCount +
                    " / " +
                    list[i].capacity +
                    "</td>" +
                    "<td>" +
                    list[i].startDate +
                    " ~ " +
                    list[i].endDate +
                    "</td>" +
                    "<td>" +
                    "<span class='status-badge " +
                    statusClass +
                    "'>" +
                    statusStr +
                    "</span>" +
                    "</td>" +
                    "</tr>";
                }

                drawPage(rep.pi);
              } else {
                tbody +=
                  "<tr>" +
                  "<td colspan='6'>조회 결과가 없습니다.</td>" +
                  "</tr>";

                $(".pagination").html("");
              }

              $("#lecture-list tbody").html(tbody);
            },
            error: function () {
              console.log("강의 목록 조회용 ajax 통신 실패");
            },
          });
        }

        // 페이징바 화면 출력용 함수
        function drawPage(pi) {
          let functionName = "ajaxFilterLectureList";
          let paging = "";

          paging +=
            "<li class='page-item " +
            (pi.currentPage == 1 ? "disabled" : "") +
            "'>" +
            "<a class='page-link link' href='#' onclick='" +
            functionName +
            "(" +
            (pi.currentPage - 1) +
            ");'" +
            ">Previous" +
            "</a>" +
            "</li>";

          for (let p = pi.startPage; p <= pi.endPage; p++) {
            if (p == pi.currentPage) {
              paging +=
                "<li class='page-item active'><a class='page-link link' onclick='" +
                functionName +
                "(" +
                p +
                ");' href='#'>" +
                p +
                "</a></li>";
            } else {
              paging +=
                "<li class='page-item'><a class='page-link link' onclick='" +
                functionName +
                "(" +
                p +
                ");' href='#'>" +
                p +
                "</a></li>";
            }
          }

          paging +=
            "<li class='page-item " +
            (pi.currentPage == pi.maxPage ? "disabled" : "") +
            "'>" +
            "<a class='page-link link' href='#' onclick='" +
            functionName +
            "(" +
            (pi.currentPage + 1) +
            ");'" +
            ">Next" +
            "</a>" +
            "</li>";

          $(".pagination").html(paging);
        }
      </script>
    </div>
  </body>
</html>
