<%@ page import="app.servlets.Bcm2WebMain" %>
<%@ page import="app.model.ViewUserSettings" %>
<%@ page import="app.servlets.NTLMUserFilter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>BCM</title>
<link href="styles/style.css" rel="stylesheet" type="text/css">



<style type="text/css">

    .demotable {
        border-collapse: collapse;
        counter-reset: schetchik 1;
    }
    .demotable tbody tr:nth-child(n+2) {
        counter-increment: schetchik;
    }
    .demotable td,
    .demotable tbody tr:before {
    }
    .demotable tbody tr:before {
        display: table-cell;
        vertical-align: middle;
    }
    .demotable tbody tr:before,
    .demotable b:after {
        content: counter(schetchik);
    }
    .demotable tbody tr:nth-child(-n):before {
        content: "";
    }

body {
	margin-left: 0%;
	margin-top: 0px;
	margin-right: 0px;
}
#LeftBar {
	float: left;
	width: 350px;
	/* [disabled]border-style: solid; */
}
#RightBar {
	float: right;
	width: 250px;
	margin-top: 5px;
	/* [disabled]border-style: solid; */
}
#HeaderPane {
	/* [disabled]border-style: dotted; */
	text-align: center;
}
#MenuPane {
	float: left;
	border-style: none;
	text-align: center;
}
#MainPane{
	margin-left: 355px;
	margin-right: 255px;
	margin-top: 5px;
	
	height: 100%;
}
#MenuItem{
	display: inline-block;
	width: 100%;
	height: 128px;
}
#ContentPane {
	height: 95%;
	margin-top: 5px;
}

</style>

<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>

<![endif]-->

    <script src="js/Moment.js"></script>
    <script src="js/ru.js"></script>

    <script src="js/script.js"></script>

    <script src = "js/ajaxquery.js"></script>

    <script src="js/bootstrap.js"></script>

    <script src="js/bootstrap-datetimepicker.js"></script>

    <link href="styles/style.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" type="text/css" href="styles/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="styles/bootstrap-datetimepicker.css">


    <script>
        function openCity(cityName) {
            var i;
            var x = document.getElementsByClassName("city");
            for (i = 0; i < x.length; i++) {
                x[i].style.display = "none";
            }
            document.getElementById(cityName).style.display = "block";
        }
        function openSystem(SysstemName) {
            var i;
            var i;
            var x = document.getElementsByClassName("SystemAction");
            for (i = 0; i < x.length; i++) {
                x[i].style.display = "none";
            }
            document.getElementById(SysstemName).style.display = "block";
        }
    </script>


</head>

<body>

<div id="RightBar"></div>
<div id="LeftBar"></div>
<div  class ="w3-light-gray w3-center">
	
	<div style="border-top-left-radius:  25px; border-top-right-radius:  25px;" class=" w3-light-blue  w3-padding">
        <a href = "profile" > <img  src="img/1486564402-settings_81520.png" align = "right" /></a>
        <h4 align="right">Привет <% out.println(NTLMUserFilter.getUserName()); %>!</h4>

	</div>

	<div class ="w3-card-4 w3-image "  style = "width: 100%; margin-top: 7px;">
		
			      	
					
  
		
		<div class="w3-card-4 w3-light-gray" style = "width: 100%;">
			<img class="w3-bar-item w3-button"  src="img/wine-bottle_icon-icons.com_60728 (2).png"
				 style = " margin-top: 8px; margin-bottom: 8px;" onClick="openCity('London')" align = "left"  />	 
  			<img class="w3-bar-item w3-button" src="img/32380cutofmeat_98860 (1).png" alt=""  style = "   margin-top: 8px; margin-bottom: 8px;" onClick="openCity('Paris')" lign="absmiddle"  />
			<img class="w3-bar-item w3-button" src="img/741cigarette_100764 (1).png"   alt=""   style = "margin-top: 8px; margin-bottom: 8px;" onClick="openCity('Tokyo')" align="right"   />
  
  
</div>
					<div id="London" class="city" style="display:none">
						
  						<div id='cssmenu' style="z-index: 0">
<ul>
   
   <li class='active has-sub'><a href='#'><span>Отгрузка</span></a>
      <ul>
         <li ><a href='#' onClick="openSystem('BacchusOut')"><span >Переотправка 31 потока</span></a></li>
          <li ><a href='#' onClick="openSystem('BacchusBuf')"><span >Действия с буфером</span></a></li>
    </ul>
         </li>
         <li class='has-sub'><a href='#'><span>Приемка</span></a>
            <ul>
               <li><a href='#' onClick="openSystem('BacchusIn')"><span>Переотправка 24 потока</span></a></li>
               
            </ul>
         </li>
    <li ><a href='#' onclick="openSystem('BacchusFlowSearch')"><span>Поиск потоков</span></a>

    </li>
    <li class='active has-sub'><a href='#'><span>Данные из БД</span></a>
        <ul>
            <li ><a href='#' onClick="openSystem('inform')"><span >Поиск марок</span></a></li>
        </ul>
    </li>
   
   
</ul>
</div>
					</div>

<div id="Paris" class="city" style="display:none">
    <div id='cssmenu' style="z-index: 0">
        <ul>

            <li ><a href='#' onClick="openSystem('CaduAuto')"><span>Автогашение</span></a>
            </li>
            <!--li class='has-sub'><a href='#'><span>Приемка</span></a>
                <ul>
                    <li><a href='#' onClick="openSystem('BacchusIn')"><span>Переотправка 24 потока</span></a></li>

                </ul>
            </li>
            <li ><a href='#' onclick="openSystem('BacchusFlowSearch')"><span>Поиск потоков</span></a>

            </li-->



        </ul>
    </div>
</div>

<div id="Tokyo" class="city" style="display:none">
    <div id='cssmenu' style="z-index: 0">
        <ul>

            <li ><a href='#' onClick="openSystem('MarkusGIS')"><span>Запросы в ГИС</span></a>
            </li>
            <!--li class='has-sub'><a href='#'><span>Приемка</span></a>
                <ul>
                    <li><a href='#' onClick="openSystem('BacchusIn')"><span>Переотправка 24 потока</span></a></li>

                </ul>
            </li>
            <li ><a href='#' onclick="openSystem('BacchusFlowSearch')"><span>Поиск потоков</span></a>

            </li-->



        </ul>
    </div>
</div>

		
		</div>




    <div id = "inform" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 " style = "display: none">
        <div class=" w3-light-blue">
            <h4 >Марки по справке</h4>
        </div>

        <form  class = "auth-info"  >
            <label>Справка Б:
                <input  id="spravka" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label>
            <label>Код SAP:
                <input id="SAPINFO" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label>
            <button class="w3-btn" onclick="tableToExcel('outputMark', 'W3C Example Table')" >Export to excel</button> <br>

        </form>
        <button id="showInfo"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="inform()" >Отправить</button>
        <div id="outputInfo">
        </div>
        <div id="textAreaInfo" style="overflow-x: scroll;">
            <!-- Trigger/Open the Modal -->
            <!-- The Modal -->

        </div>


    </div>
            
				
	<div id = "BacchusOut" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 " style = "display: none">
        <div class=" w3-light-blue">
	<h4 >Переотправка 31 потока</h4>
        </div>
            <!--method="GET" action="" target = "my_frame" -->
                    <form  class = "auth-info"  >
                        <label>Номер буфера:
                            <input  id="buff" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                        </label>
                        <label>Код РЦ:
                            <input id="SAP" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                        </label><br>
                        <input id="check0" class="w3-check" type="checkbox">
                        <label >Подтверждение отправки</label>

                        <input id="check1" class="w3-check" type="checkbox" onclick="onOff()">
                        <label>Переотправить ошибочные</label>
                        <input id="check2" class="w3-check" type="checkbox">
                        <label>Редактирование перед отправкой</label>
                        </form>
        <button id="send31"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="out31()" >Отправить</button>



        <div id="output">
        </div>
        <div id="textArea" >
            <!-- Trigger/Open the Modal -->
            <!-- The Modal -->

        </div>
    </div>
    <div id = "BacchusBuf" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 " style = "display: none">
        <div class=" w3-light-blue">
            <h4 >Действия с буфером</h4>
        </div>
        <!--method="GET" action="" target = "my_frame" -->
        <form  class = "auth-info"  >
            <label>Номер буфера:
                <input  id="Actionbuff" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label>
            <label>Код РЦ:
                <input id="ActionSAP" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label><br>
            <select id="changeList" class="w3-select w3-animate-opacity" name="option" style="display: none">
                <option value="" disabled selected>Выберите статус</option>
                <option value="294">294(Создан)</option>
                <option value="295">295(Ошибка)</option>
                <option value="527">527(Отправлен в ЕГАИС)</option>
                <option value="528">528(Успешно создан в ЕГАИС)</option>
                <option value="302">302(Передан в SAP)</option>
                <option value="303">303(Удален)</option>
            </select>
            <input id="Actioncheck0" class="w3-check" type="checkbox">
            <label >Снять резервы</label>

            <input id="Actioncheck1" class="w3-check" type="checkbox">
            <label>Смена статуса марок</label>
            <input id="Actioncheck2" class="w3-check" type="checkbox" onclick="showChnage()">
            <label>Смена статуса буфера</label>
        </form>
        <button id="Actionsend31"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="actionBuf()" >Выполнить</button>



        <div id="Actionoutput">

        </div>


        <div id="ActiontextArea" >
            <!-- Trigger/Open the Modal -->
            <!-- The Modal -->

        </div>
    </div>

    <div id = "BacchusFlowSearch" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 " style = "display: none">
        <div class=" w3-light-blue">
            <h4 >Поиск потоков</h4>
        </div>
        <!--method="GET" action="" target = "my_frame" -->
        <form  class = "auth-info"  >
            <label>Номер буфера:
                <input  id="buffSearch" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label>
            <label>Код РЦ:
                <input id="SAPSearch" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label><br>
        </form>
        <button id="SearchFlow"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="fLowSearch()" >Найти</button>



        <div id="outputSearch">
        </div>
        <div id="textAreaSearch" >
            <!-- Trigger/Open the Modal -->
            <!-- The Modal -->

        </div>
    </div>


    <div id = "MarkusGIS" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 " style = "display: none">
        <div class="w3-card-4 w3-light-gray">
            <button class="w3-bar-item w3-button tablink w3-light-blue" onclick="cityOpen(event, 'CheckOwner')">Проверка собственника</button>
            <button  class="w3-bar-item w3-button tablink " onclick="cityOpen(event, 'ReqDet')" disabled>Запрос детализации</button>
        </div>
        <div id="CheckOwner" class="cityM">
            <div class=" w3-light-blue">
                <h4 >Проверка собственника КИЗ</h4>
            </div>
            <!--method="GET" action="" target = "my_frame" -->
            <label id="cisStatus">Статус КИЗ:</label>
            <form  class = "auth-info"  >
                <label>КИЗ:
                    <input  id="cis" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                </label>
                <label>ИНН:
                    <input id="inn" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                </label><br>
            </form>
            <button id="CheckCis"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="CheckCis()" >Отправить</button>

        </div>
        <div id="ReqDet" class="cityM" style="display:none">
            <div class=" w3-light-blue">
                <h4 >Запрос детализации</h4>
            </div>
            <!--method="GET" action="" target = "my_frame" -->
            <form  class = "auth-info"  >
                <label>КИЗ:
                    <input  id="cis" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                </label>
                <label>ИНН:
                    <input id="inn" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                </label><br>
            </form>
            <button id="CheckCis"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="CheckCis()" >Отправить</button>

        </div>




        <div id="outputCheck">
        </div>
        <div id="textAreaCheck" >
            <!-- Trigger/Open the Modal -->
            <!-- The Modal -->

        </div>
    </div>
    <div id = "CaduAuto" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 " style = "display: none">
        <div class=" w3-light-blue">
            <h4 >Автогашение</h4>
        </div>


        <!--method="GET" action="" target = "my_frame" -->
        <!--form--  class = "auth-info"  >
            <label>Номер буфера:
                <input  id="buff" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label>
            <label>Код РЦ:
                <input id="SAP" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
            </label><br>
            <input id="check0" class="w3-check" type="checkbox">
            <label >Подтверждение отправки</label>

            <input id="check1" class="w3-check" type="checkbox" onclick="onOff()">
            <label>Переотправить ошибочные</label>
            <input id="check2" class="w3-check" type="checkbox">
            <label>Редактирование перед отправкой</label>
        </form-->

        <!--button id="ShowTableAuto"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="showTempTable()" >Просмотр временной таблицы</button>
        <button id="ClearTableAuto"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="trunAutoGas()" >Очистить временную таблицу</button>
        <button id="InsToTableAuto"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="insMainAutoGas()" >Добавить сертификаты в таблицу автогашения</button>
        <button id="ShowMainTableAuto"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="showTableAutoGas()" >Просмотр таблицы автогашения</button!-->


        <button id="InsCertTableAuto"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="insToAutoGas2()" >Добавить сертификаты</button>



        <div id="outputAuto" class="w3-container  w3-small" style="display: none; overflow-x: scroll; overflow-y: scroll;height: 55%">
        </div>
        <div id="textAreaAuto" >
            <!-- Trigger/Open the Modal -->
            <!-- The Modal -->

        </div>
    </div>

    <div id = "BacchusIn" class="SystemAction w3-panel w3-light-gray w3-display-container w3-card-4 " style = "display: none">
            <div class=" w3-light-blue">
                <h4 >Переотправка 24 потока</h4>
            </div>

            <form  class = "auth-info"  >
                <label>Номер буфера:
                    <input  id="buff24" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                </label>
                <label>Код РЦ:
                    <input id="SAP24" type="text"  class="w3-input w3-animate-input w3-border w3-round-medium" style="width: 30%">
                </label><br>
                <input id="24check0" class="w3-check" type="checkbox">
                <label >Подтверждение отправки</label>
                <input id="24check1" class="w3-check" type="checkbox">
                <label>Редактирование перед отправкой</label>
            </form>
            <button id="send24"  class="w3-btn w3-green w3-round-large w3-margin-bottom" onclick="in24()" >Отправить</button>
            <div id="output24">
            </div>
            <div id="textArea24" >
                <!-- Trigger/Open the Modal -->
                <!-- The Modal -->

            </div>


        </div>
                
		
				            
            
        
        
	
	
	
	
</div>


</body>
</html>
