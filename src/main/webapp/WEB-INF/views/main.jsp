<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${ pageContext.request.contextPath }" />
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>LiveKlass-수강신청관리시스템</title>

    <!-- jQuery 라이브러리 -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <!-- 부트스트랩에서 제공하고 있는 스타일 -->
    <link
      rel="stylesheet"
      href="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
    />
    <!-- 부트스트랩에서 제공하고 있는 스크립트 -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"></script>

    <style>
      body {
        font-family: Arial;
        /* background: #f3f4f6; */
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
      }

      .system-title {
        padding: 30px;
        margin: 30px 0px;
        width: 300px;
      }

      .btn-outline-dark {
        margin-right: 10px;
      }
    </style>
  </head>

  <body>
    <div class="system-title">
      <h2>수강 신청 시스템</h2>
    </div>

    <div class="d-flex mr-3">
      <a
        class="btn btn-lg btn-outline-dark"
        href="${ contextPath }/lecture/list.page"
      >
        강의 관리
      </a>
      <a
        class="btn btn-lg btn-primary"
        href="${ contextPath }/enrollment/list.page"
      >
        수강 신청 관리
      </a>
    </div>
  </body>
</html>
