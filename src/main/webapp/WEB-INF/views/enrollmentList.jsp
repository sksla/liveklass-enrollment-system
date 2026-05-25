<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${ pageContext.request.contextPath }" />
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>수강 신청 관리</title>

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

      .table td,
      .table th {
        vertical-align: middle;
      }

      .status-badge {
        padding: 5px 10px;
        border-radius: 5px;
        color: white;
        font-size: 13px;
      }

      .pending {
        background: #ffc107;
        color: black;
      }

      .confirmed {
        background: #28a745;
      }

      .cancelled {
        background: #dc3545;
      }

      .table-scroll {
        max-height: 400px;
        overflow-y: auto;
      }

      .table-scroll thead th {
        position: sticky;
        top: 0;
        background: #f8f9fa;
        z-index: 1;
      }
    </style>
  </head>

  <body>
    <div class="wrap">
      <!-- 제목 -->
      <div class="page-title d-flex justify-content-between align-items-center">
        <h2>수강 신청 관리</h2>

        <a href="${ contextPath }" class="btn btn-outline-dark"> 메인으로 </a>
      </div>

      <!-- 신청 가능한 강의 목록 -->
      <div class="card-area">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h4>신청 가능한 강의</h4>
        </div>

        <!-- 스크롤 영역 -->
        <div class="table-scroll">
          <table id="lecture-table" class="table">
            <thead class="thead-light">
              <tr>
                <th width="20%">강의명</th>
                <th width="10%">강사명</th>
                <th width="15%">가격</th>
                <th width="10%">정원</th>
                <th width="20%">수강기간</th>
                <th width="10%">상태</th>
                <th width="15%">신청</th>
              </tr>
            </thead>

            <tbody>
              <tr>
                <td>Spring Boot 입문</td>

                <td>강사명</td>

                <td>100,000원</td>

                <td>10 / 30</td>

                <td>2026-06-01 ~ 2026-07-31</td>

                <td>모집중</td>

                <td>
                  <button class="btn btn-primary btn-sm">수강 신청</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 내 수강 신청 목록 -->
      <div class="card-area">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h4>내 수강 신청 목록</h4>
        </div>

        <table id="my-enrollment-table" class="table table-hover">
          <thead class="thead-light">
            <tr>
              <th width="20%">강의명</th>
              <th width="10%">정원</th>
              <th width="10%">신청 상태</th>
              <th width="20%">신청일</th>
              <th width="20%">결제확정일/취소일</th>
              <th width="20%">관리</th>
            </tr>
          </thead>

          <tbody>
            <!-- PENDING -->
            <tr>
              <td>Spring Boot 입문</td>
              <td>1 / 30</td>

              <td>
                <span class="status-badge pending"> 결제 대기 </span>
              </td>

              <td>2026-05-22 13:10</td>

              <td>-</td>

              <td>
                <button class="btn btn-success btn-sm">결제 확정</button>

                <button class="btn btn-outline-danger btn-sm">신청 취소</button>
              </td>
            </tr>

            <!-- CONFIRMED -->
            <tr>
              <td>MySQL 실전</td>
              <td>1 / 30</td>

              <td>
                <span class="status-badge confirmed"> 수강 확정 </span>
              </td>

              <td>2026-05-20 09:30</td>

              <td>2026-05-20 09:35</td>

              <td>
                <button class="btn btn-outline-danger btn-sm">수강 취소</button>
                <!-- 결제취소일 지났을때 -->
                <!-- <button class="btn btn-secondary btn-sm" disabled>취소 기간 만료</button> -->
              </td>
            </tr>

            <!-- CANCELLED -->
            <tr>
              <td>Java 기초</td>
              <td>1 / 30</td>

              <td>
                <span class="status-badge cancelled"> 취소됨 </span>
              </td>

              <td>2026-05-18 14:00</td>

              <td>2026-05-18 14:05</td>

              <td>-</td>
            </tr>
          </tbody>
        </table>
        <br />
        <div id="pagingArea">
          <ul class="pagination justify-content-center">
            <li class="page-item disabled">
              <a class="page-link" href="#">Previous</a>
            </li>
            <li class="page-item"><a class="page-link" href="#">1</a></li>
            <li class="page-item"><a class="page-link" href="#">2</a></li>
            <li class="page-item"><a class="page-link" href="#">3</a></li>
            <li class="page-item"><a class="page-link" href="#">4</a></li>
            <li class="page-item"><a class="page-link" href="#">5</a></li>
            <li class="page-item"><a class="page-link" href="#">Next</a></li>
          </ul>
        </div>
      </div>
    </div>

    <script>
      $(document).ready(function () {
        ajaxSelectLectureList();
        ajaxSelectMyEnrollmentList(1);
      });

      // ajax 내 수강 신청 목록 조회용 function
      function ajaxSelectMyEnrollmentList(requestPage) {
        $.ajax({
          url: "${contextPath}/enrollment/enrollmentList.do",
          type: "post",
          async: false,
          data: { page: requestPage },
          success: function (rep) {
            let list = rep.list;
            let tbody = "";

            if (list.length > 0) {
              for (let i = 0; i < list.length; i++) {
                let e = list[i];

                let statusStr = "";
                let statusClass = "";
                let buttonHtml = "";
                let confirmCancelDate = "";

                // 1. 상태별 분기
                if (e.enrollStatus == "PENDING") {
                  statusStr = "결제 대기";
                  statusClass = "pending";
                  confirmCancelDate = "-";

                  buttonHtml =
                    "<button class='btn btn-success btn-sm confirm-btn' " +
                    "data-lec-id='" +
                    e.lecId +
                    "' " +
                    "data-enroll-status='" +
                    e.enrollStatus +
                    "'>결제 확정</button> " +
                    "<button class='btn btn-outline-danger btn-sm cancel-btn' " +
                    "data-lec-id='" +
                    e.lecId +
                    "' " +
                    "data-enroll-status='" +
                    e.enrollStatus +
                    "' " +
                    "data-confirmed-at=''>신청 취소</button>";
                } else if (e.enrollStatus == "CONFIRMED") {
                  statusStr = "수강 확정";
                  statusClass = "confirmed";
                  confirmCancelDate = e.confirmedAt;

                  buttonHtml =
                    "<button class='btn btn-outline-danger btn-sm cancel-btn' " +
                    "data-lec-id='" +
                    e.lecId +
                    "' " +
                    "data-enroll-status='" +
                    e.enrollStatus +
                    "' " +
                    "data-confirmed-at='" +
                    e.confirmedAt +
                    "'>수강 취소</button>";
                } else {
                  // CANCELLED

                  statusStr = "취소됨";
                  statusClass = "cancelled";

                  confirmCancelDate = e.cancelledAt;

                  buttonHtml = "-";
                }

                // 2. 행 생성
                tbody +=
                  "<tr>" +
                  "<td>" +
                  e.lecTitle +
                  "</td>" +
                  "<td>" +
                  e.currentEnrollmentCount +
                  " / " +
                  e.capacity +
                  "</td>" +
                  "<td><span class='status-badge " +
                  statusClass +
                  "'>" +
                  statusStr +
                  "</span></td>" +
                  "<td>" +
                  e.createdAt +
                  "</td>" +
                  "<td>" +
                  confirmCancelDate +
                  "</td>" +
                  "<td>" +
                  buttonHtml +
                  "</td>" +
                  "</tr>";
              }
            } else {
              tbody = "<tr><td colspan='6'>조회 결과가 없습니다.</td></tr>";
            }

            $("#my-enrollment-table tbody").html(tbody);

            // paging 처리
            drawPage(rep.pi);
          },
          error: function () {
            console.log("수강신청 목록 조회용 ajax 통신 실패");
          },
        });
      }

      // 페이징바 화면 출력용 함수
      function drawPage(pi) {
        let functionName = "ajaxSelectMyEnrollmentList";
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

      // ajax 수강 신청 가능한 강의 목록 조회용 function
      function ajaxSelectLectureList() {
        $.ajax({
          url: "${contextPath}/enrollment/lectureList.do",
          type: "post",
          async: false,
          success: function (rep) {
            //console.log(rep);

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

                tbody +=
                  "<tr>" +
                  "<td>" +
                  list[i].lecTitle +
                  "</td>" +
                  "<td>" +
                  list[i].memName +
                  "</td>" +
                  "<td>" +
                  Number(list[i].price).toLocaleString() +
                  "</td>" +
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
                  statusStr +
                  "</td>" +
                  '<td><button class="btn btn-primary btn-sm enroll-btn" ' +
                  'data-lec-id="' +
                  list[i].lecId +
                  '">수강 신청</button></td>';
                ("</tr>");
              }
            } else {
              tbody +=
                "<tr>" + "<td colspan='6'>조회 결과가 없습니다.</td>" + "</tr>";
            }

            $("#lecture-table tbody").html(tbody);
          },
          error: function () {
            console.log("신청 가능한 강의 목록 조회용 ajax 통신 실패");
          },
        });
      }

      // 수강 신청 버튼 클릭 시
      $(document).on("click", ".enroll-btn", function () {
        let lecId = $(this).data("lec-id");

        if (!confirm("수강 신청하시겠습니까?")) return;

        $.ajax({
          url: "${contextPath}/enrollment/enroll.do",
          type: "post",
          data: {
            lecId: lecId,
            memId: 3,
          }, // memId는 로그인 구현 시 세션에서
          success: function (rep) {
            alert(rep.alertMsg);
            ajaxSelectLectureList();
            ajaxSelectMyEnrollmentList(1);
          },
          error: function () {
            console.log("수강 신청 ajax 통신 실패");
          },
        });
      });

      // 결제 확정 버튼 클릭 시
      $(document).on("click", ".confirm-btn", function () {
        let lecId = $(this).data("lec-id");

        if (!confirm("결제 확정하시겠습니까?")) return;

        $.ajax({
          url: "${contextPath}/enrollment/confirm.do",
          type: "post",
          data: {
            lecId: lecId,
            memId: 3,
          },
          success: function (rep) {
            alert(rep.alertMsg);
            ajaxSelectLectureList();
            ajaxSelectMyEnrollmentList(1);
          },
          error: function () {
            console.log("결제 확정 ajax 통신 실패");
          },
        });
      });

      // 수강 취소 버튼 클릭시
      $(document).on("click", ".cancel-btn", function () {
        let lecId = $(this).data("lec-id");
        let enrollStatus = $(this).data("enroll-status");
        let confirmedAt = $(this).data("confirmed-at");

        if (!confirm("취소하시겠습니까?")) return;

        $.ajax({
          url: "${contextPath}/enrollment/cancel.do",
          type: "post",
          data: {
            lecId: lecId,
            memId: 3,
            enrollStatus: enrollStatus,
            confirmedAt: confirmedAt,
          },
          success: function (rep) {
            alert(rep.alertMsg);
            ajaxSelectLectureList();
            ajaxSelectMyEnrollmentList(1);
          },
          error: function () {
            console.log("수강 취소 ajax 통신 실패");
          },
        });
      });
    </script>
  </body>
</html>
