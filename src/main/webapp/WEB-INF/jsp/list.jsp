<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="common/tag.jsp" %>
<html>
<head>
    <title>秒杀列表页</title>
    <%@ include file="common/head.jsp"%>
</head>
<body>
    <div class="container">
        <div class="panel panel-default">
            <div class="panel panel-heading text-center">
                <h2>秒杀列表页</h2>
            </div>
            <div class="panel panel-body">
                <table class="table table-default">
                    <thead>
                        <tr>
                            <th>名称</th>
                            <th>库存</th>
                            <th>开始时间</th>
                            <th>结束时间</th>
                            <th>创建时间</th>
                            <th>详情页</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="s" items="${list}">
                            <tr>
                                <td>${s.name}</td>
                                <td>${s.number}</td>
                                <td>
                                    <fmt:formatDate value="${s.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${s.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${s.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <a class="btn btn-info" href="/seckill/${s.seckillId}/detail" target="_blank">前往详细</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
<script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
<script src="http://cdn.static.runoob.com/libs/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</html>
