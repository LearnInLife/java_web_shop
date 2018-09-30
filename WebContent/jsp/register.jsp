<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>会员注册</title>
<link rel="stylesheet" href="css/bootstrap.min.css" type="text/css" />
<script src="js/jquery-1.11.3.min.js" type="text/javascript"></script>
<!-- 引入表单校验jquery插件 -->
<script src="js/jquery.validate.min.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<!-- 引入自定义css文件 style.css -->
<link rel="stylesheet" href="css/style.css" type="text/css" />

<style>
body {
	margin-top: 20px;
	margin: 0 auto;
}

.carousel-inner .item img {
	width: 100%;
	height: 300px;
}

font {
	color: #3164af;
	font-size: 18px;
	font-weight: normal;
	padding: 0 10px;
}

.error{
	color:red
}
</style>

</head>

<body>
	<jsp:include page="/jsp/header.jsp"></jsp:include>
	
	<div class="container"
		style="width: 100%; background: url('image/regist_bg.jpg');">
		<div class="row">
			<div class="col-md-2"></div>
			<div class="col-md-8"
				style="background: #fff; padding: 40px 80px; margin: 30px; border: 7px solid #ccc;">
				<font>会员注册</font>USER REGISTER
				<form id="myform" class="form-horizontal" style="margin-top: 5px;">
					<div class="form-group">
						<label for="username" class="col-sm-2 control-label">用户名</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="username" name="username"
								placeholder="请输入用户名">
						</div>
					</div>
					<div class="form-group">
						<label for="inputPassword3" class="col-sm-2 control-label">密码</label>
						<div class="col-sm-6">
							<input type="password" class="form-control" id="password" name="password"
								placeholder="请输入密码">
						</div>
					</div>
					<div class="form-group">
						<label for="confirmpwd" class="col-sm-2 control-label">确认密码</label>
						<div class="col-sm-6">
							<input type="password" class="form-control" id="confirmpwd" name="repassword"
								placeholder="请输入确认密码">
						</div>
					</div>
					<div class="form-group">
						<label for="inputEmail3" class="col-sm-2 control-label">Email</label>
						<div class="col-sm-6">
							<input type="email" class="form-control" id="email" name="email"
								placeholder="Email">
						</div>
					</div>
					<div class="form-group">
						<label for="usercaption" class="col-sm-2 control-label">姓名</label>
						<div class="col-sm-6">
							<input type="text" class="form-control" id="usercaption" name="name"
								placeholder="请输入姓名">
						</div>
					</div>
					<div class="form-group opt">
						<label for="inlineRadio1" class="col-sm-2 control-label">性别</label>
						<div class="col-sm-6">
							<label class="radio-inline"> 
								<input type="radio" name="sex" id="sex1" value="male" >男
							</label> 
							<label class="radio-inline"> 
								<input type="radio" name="sex" id="sex2" value="female">女
							</label>
							<label class="error" for="sex" style="display:none ">您没有第三种选择</label>
						</div>
					</div>
					<div class="form-group">
						<label for="date" class="col-sm-2 control-label">出生日期</label>
						<div class="col-sm-6">
							<input type="date" class="form-control" id="birthday" name="birthday">
						</div>
					</div>

					<div class="form-group">
						<label for="date" class="col-sm-2 control-label">验证码</label>
						<div class="col-sm-3">
							<input type="text" class="form-control" id="checkCode" name="checkCode">

						</div>
						<div class="col-sm-2">
							<img id="checkImg" src="${pageContext.request.contextPath}/checkImg"/>
						</div>

					</div>

					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-10" id="submit">
							<input type="button" width="100" value="注册"
								style="background: url('./images/register.gif') no-repeat scroll 0 0 rgba(0, 0, 0, 0); height: 35px; width: 100px; color: white;">
						</div>
					</div>
				</form>
				<c:if test="${!empty error_msg }">
					<div style="color:red">${error_msg}</div>
				</c:if>
			</div>

			<div class="col-md-2"></div>

		</div>
	</div>
	<!-- 引入footer.jsp -->
	<jsp:include page="/jsp/footer.jsp"></jsp:include>
	
	<script type="text/javascript">

		//自定义校验规则
		$.validator.addMethod(
			//规则的名称
			"checkUsername",
			//校验的函数
			function(value,element,params){
				//定义一个标志
				var flag = false;
				//value:输入的内容
				//element:被校验的元素对象
				//params：规则对应的参数值
				//目的：对输入的username进行ajax校验
				$.ajax({
					"async":false,
					"url":"${pageContext.request.contextPath}/checkUsername",
					"data":{"username":value},
					"type":"POST",
					"dataType":"json",
					"success":function(data){
						flag = data.isExist;
					}
				});
				//返回false代表该校验器不通过
				return !flag;
			}
		);
	
	$(function(){
		
		$("#myform").validate({
			rules:{
				"username":{
					"required":true,
					"checkUsername":true
				},
				"password":{
					"required":true,
					"rangelength":[6,12]
				},
				"repassword":{
					"required":true,
					"rangelength":[6,12],
					"equalTo":"#password"
				},
				"email":{
					"required":true,
					"email":true
				},
				"sex":{
					"required":true
				}
			},
			"messages":{
				"username":{
					"required":"用户名不能为空",
					"checkUsername":"用户名已经存在"
				},
				"password":{
					"required":"密码不能为空",
					"rangelength":"密码长度6-12位"
				},
				"repassword":{
					"required":"密码不能为空",
					"rangelength":"密码长度6-12位",
					"equalTo":"两次密码不一致"
				},
				"email":{
					"required":"邮箱不能为空",
					"email":"邮箱格式不正确"
				}
			}
		});
		
		$("#checkImg").click(function(){
			/* 新旧图片的地址是一样的，而导致浏览器自动读缓存。在地址后面加上不同参数即可 */
			$("#checkImg").attr("src","${pageContext.request.contextPath}/checkImg?time="+new Date().getTime());
		});
		
		$.ajaxSetup({
			 complete:function(XMLHttpRequest, textStatus){
				// 通过XMLHttpRequest取得响应头，REDIRECT      
			 	var redirect = XMLHttpRequest.getResponseHeader("REDIRECT");//若HEADER中含有REDIRECT说明后端想重定向
			 	if (redirect == "REDIRECT") {  
		            var win = window;      
		            while (win != win.top){    
		                win = win.top;    
		            } 
		            win.location.href= XMLHttpRequest.getResponseHeader("CONTEXTPATH");
		        }  
			 }
		});
		
		$("#submit").click(function(){
			var flag = $("#myform").valid();
			if (!flag) {
				 //没有通过验证
				return;
			}
			var username = $("#username").val();
			var password = $("#password").val();
			var email = $("#email").val();
			var usercaption = $("#usercaption").val();
			var sex = $("input[name='sex']:checked").val();
			var date = $('#birthday').val();
			var checkcode = $('#checkCode').val()
			
			$.ajax({
				url:"${pageContext.request.contextPath}/regist",
				type:"POST",
				async:false,
				data:{
					username:username,
					password:password,
					email:email,
					name:usercaption,
					sex:sex,
					birthday:date,
					checkcode:checkcode
				},
				dataType:"json",
				success:function(data){
					alert(data.error_msg);
				}
			});
		});
	});
	
</script>
	
</body>