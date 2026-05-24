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
    </style>
  </head>

  <body>
    <div class="wrap">
      <!-- 제목 -->
      <div class="page-title d-flex justify-content-between align-items-center">
        <h2>강의 관리</h2>

        <a href="${ contextPath }" class="btn btn-outline-dark"> 메인으로 </a>
      </div>

      <!-- 강의 등록 -->
      <div class="card-area">
        <h4 class="mb-4">강의 등록</h4>

        <form
          id="enrollForm"
          method="post"
          action="${ contextPath }/lecture/lectureRegist.do"
        >
          <input type="hidden" name="creatorId" value="1" />
          <div class="form-group">
            <label>강의 제목</label>
            <input
              type="text"
              id="lecTitle"
              name="lecTitle"
              class="form-control"
              required
            />
          </div>

          <div class="form-group">
            <label>강의 설명</label>
            <textarea
              id="description"
              class="form-control"
              rows="4"
              name="description"
            ></textarea>
          </div>

          <div class="form-row">
            <div class="form-group col-md-4">
              <label>가격</label>
              <input
                type="number"
                id="price"
                name="price"
                class="form-control"
                min="1"
                required
              />
            </div>

            <div class="form-group col-md-4">
              <label>최대 정원</label>
              <input
                type="number"
                id="capacity"
                name="capacity"
                class="form-control"
                min="1"
                required
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group col-md-6">
              <label>수강 시작일</label>
              <input
                type="date"
                id="startDate"
                name="startDate"
                class="form-control"
                required
              />
            </div>

            <div class="form-group col-md-6">
              <label>수강 종료일</label>
              <input
                type="date"
                id="endDate"
                name="endDate"
                class="form-control"
                required
              />
            </div>
          </div>

          <div class="text-right">
            <button
              type="submit"
              onclick="return registValidate();"
              class="btn btn-primary"
            >
              강의 등록
            </button>
          </div>
        </form>
      </div>

      <script>
        $(document).ready(function () {
          let today = new Date();

          let year = today.getFullYear();
          let month = String(today.getMonth() + 1).padStart(2, "0");
          let day = String(today.getDate()).padStart(2, "0");

          today = year + "-" + month + "-" + day;

          $("#startDate").attr("min", today);
          $("#endDate").attr("min", today);
        });

        // 등록 유효성 검사
        function registValidate() {
          let startDate = new Date($("#startDate").val());
          let endDate = new Date($("#endDate").val());

          let price = Number($("#price").val());
          let capacity = Number($("#capacity").val());

          if ($("#lecTitle").val().trim() == "") {
            alert("제목을 입력해주세요");
            return false;
          } else if ($("#description").val().trim() == "") {
            alert("설명을 입력해주세요");
            return false;
          } else if (price <= 0) {
            alert("가격은 0보다 커야 합니다.");
            return false;
          } else if (capacity <= 0) {
            alert("최대 정원은 1명 이상이어야 합니다.");
            return false;
          } else if (startDate > endDate) {
            alert("수강 종료일은 수강 시작일보다 이후여야 합니다.");
            return false;
          }

          return true;
        }
      </script>
    </div>
  </body>
</html>
