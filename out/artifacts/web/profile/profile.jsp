<%--
  Created by IntelliJ IDEA.
  User: M.Moiseev
  Date: 26.05.2020
  Time: 23:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>



<%@ page import="app.servlets.NTLMUserFilter" %>
<html>
<head>
    <title>

        Настройки профиля <% out.println(NTLMUserFilter.getUserName()); %>
    </title>

    <script src="js/Moment.js"></script>

    <script src="js/ru.js"></script>

    <script src="js/script.js"></script>

    <script src = "js/ajaxquery.js"></script>

    <script src="js/bootstrap.js"></script>

    <script src="js/bootstrap-datetimepicker.js"></script>

    <link href="styles/style.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" type="text/css" href="styles/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="styles/bootstrap-datetimepicker.css">
    <style>
        #snackbar {
            visibility: hidden;
            min-width: 250px;
            margin-left: -125px;
            background-color: #333;
            color: #fff;
            text-align: center;
            border-radius: 2px;
            padding: 16px;
            position: fixed;
            z-index: 1;
            left: 50%;
            bottom: 30px;
            font-size: 17px;
        }

        #snackbar.show {
            visibility: visible;
            -webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
            animation: fadein 0.5s, fadeout 0.5s 2.5s;
        }

        @-webkit-keyframes fadein {
            from {bottom: 0; opacity: 0;}
            to {bottom: 30px; opacity: 1;}
        }

        @keyframes fadein {
            from {bottom: 0; opacity: 0;}
            to {bottom: 30px; opacity: 1;}
        }

        @-webkit-keyframes fadeout {
            from {bottom: 30px; opacity: 1;}
            to {bottom: 0; opacity: 0;}
        }

        @keyframes fadeout {
            from {bottom: 30px; opacity: 1;}
            to {bottom: 0; opacity: 0;}
        }
    </style>



</head>
<body onload='loadUserParams()'>
<div  class ="w3-light-gray w3-center">
    <div style="border-top-left-radius:  25px; border-top-right-radius:  25px;" class=" w3-light-blue  w3-padding">
        <a href = "/" > <img  src="img/homeflat_106039.png" align = "right" /></a>
        <h4 align="right">Настройки профиля</h4><i class="fa fa-home"></i>
    </div>
    <div id = "mainInfo" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 ">

        <!--method="GET" action="" target = "my_frame" -->
        <div id="userInfo">



        </div>
        <div id="logs" class="w3-container  w3-small" style="display: none">
            <div class="w3-row-padding" >
                <div class="w3-quarter" >
                    <select id="userinput" class="w3-select" name="option" onchange="viewLogs()">
                        <option value="def" disabled selected>Пользователь</option>
                        <option value="all">Все</option>
                    </select>
                </div>
                <div class="w3-quarter" >
                    <select id="systemOpt" class="w3-select" name="option" onchange="viewLogs()">
                        <option value="def" disabled selected>Система</option>
                        <option value="all">Все</option>
                        <option value="BACCHUS">БАХУС</option>
                        <option value="CADUCEUS">КАДУЦЕЙ</option>
                        <option value="MARKUS">МАРКУС</option>
                        <option value="BCM">BCM</option>
                    </select>
                </div>
                <div class="w3-quarter" >
                    <select id="statusOpt" class="w3-select" name="option" onchange="viewLogs()">
                        <option value="def" disabled selected>Статус</option>
                        <option value="all">Все</option>
                        <option value="OK">OK</option>
                        <option value="ERROR">ERROR</option>
                        <option value="CANCELED">CANCELED</option>
                    </select>
                </div>
                <div class="w3-quarter" >
                    <select id="period" class="w3-select" name="option" onchange="viewLogs()" onfocus="this.selectedIndex = -1;">
                        <option value="def" disabled selected>Период</option>
                        <option value="all">Все</option>
                        <option value="day">Текущий день</option>
                        <option value="3day">За 3 дня</option>
                        <option value="period">Указать период</option>
                    </select>
                </div>


            </div>

               <div id="calFrom" class='col-md-5' style="width: 50%; display: none">
                    <div class="form-group">
                        <div class='input-group date' id='datetimepicker1'>
                            <input  id="dateFrom" type='text' class="form-control" oninput=""  />
                            <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
                        </div>
                    </div>
                </div>
               <div id="calTo" class='col-md-5' style="width: 50%; display: none">
                    <div class="form-group">
                        <div class='input-group date' id='datetimepicker2'>
                            <input  id="dateTo" type='text' class="form-control" />
                            <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
                        </div>
                    </div>
                </div>


        </div>

        <div id="tablelogs" class="w3-container  w3-small" style="display: none; overflow-x: scroll;height: 55%">
        </div>
        <div id="modalChange" >
        <!-- Trigger/Open the Modal -->
        <!-- The Modal -->

    </div>
    </div>
</div>
<br/>
<!-- padding for jsfiddle -->

<script type="text/javascript">
    var inp = document.getElementById('dateFrom');
    inp.oninput=func;


    function func(){
        alert(inp.value);
    }

    $(function () {
        $('#datetimepicker1').datetimepicker();
        $('#datetimepicker2').datetimepicker();
        locale: 'ru'
    });

</script>
</body>
</html>
