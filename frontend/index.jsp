<%--
  Created by IntelliJ IDEA.
  User: Jakob Engl
  Date: 11.11.2018
  Time: 21:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <style>
        table, th, td {
            border: 1px solid black;
        }

        #container1, container2, container3, container4,  {
            background-color: yellow;
        }

    </style>

    <title>
        trainigsplan.com
    </title>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="vendor/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" type="text/css" href="vendor/animate/animate.css">
    <link rel="stylesheet" type="text/css" href="vendor/select2/select2.min.css">
    <link rel="stylesheet" type="text/css" href="vendor/perfect-scrollbar/perfect-scrollbar.css">
    <link rel="stylesheet" type="text/css" href="css/util.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">


    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script>
        $(function(){
            $('#btn').on('click', function()
                {
                    alert('Clicked - jquery');
                }
            );
        });
    </script>
</head>
<body>

<div style="display: flex; justify-content: space-between; position:fixed; width:100%">
    <button onclick="addTable(true)" class="button" style="top: 10px;">Add new Day</button>
    <button onclick="receivePlanfromDB(false)" class="button" style=" top: 10px; ">Load Plan</button>
    <button onclick="loadPlanIntoDB()" class="button" style="top: 10px;">Save Changes</button>
    <button onclick="cleanUpTables()"class="button" style="top: 10px;">New Plan</button>
    <button onclick="logout()"class="button" value="username ..." id="username" style="top: 10px;"></button>
</div>​


<div id="dialog" style="position: fixed; display:none; z-index: 10; text-align: center; width:100vw; margin:0 auto;">
    <dialog id="favDialog" style="margin: 0 auto;">
        <form method="dialog">
            <p>
                <label class="button" id="test-dropdown2" >Choose trainingsplan:
                    <select id="test-dropdown" class="button">
                    </select>
                </label>
            </p>
            <menu style="display: flex; justify-content: center;">
                <button id="cancel" class="button" style="display:inline-block;">Cancel</button>
                <button id="confirm" class="button" style="display:inline-block;">Confirm</button>
            </menu>
        </form>
    </dialog>

    <output>
        <!---->
    </output>
</div>

<div id="container1" style="width: 100%;"></div>
<div id="container2" style="width: 100%;"></div>
<div id="container3" style="width: 100%;"></div>
<div id="container4" style="width: 100%;"></div>
<div id="container5" style="width: 100%;"></div>

<script>

    var numtables = 0;
    var loaded = false;     // dont reload all plans by user again
    var planname = '';      // needed to determine wheter a plan should be completely stored in db or only updated, if planname.length = 0 - new plan else update
    var currentPlanId = ''; // Needed to send to backend which planid should be replace - going to be deprecated when no new plan is created anymore

    // get the first button by it's ID, and listen to the click event
    // add an empty table to the container div
    function addTable(addRowByDefault)
    {
        // column 3 2 times, maybe change at a later point
        var basicTable1 = '<div class="limiter" id="day1"> <div class="container-table100"> <div class="wrap-table100"> <div class="table100 ver3 m-b-110"> <div class="table100-head"> <table> <thead> <tr class="row100 head"> <th class="cell100 column1" style="width:16.6%; text-align: center">Exercise</th> <th class="cell100 column2" style="width:16.6%; text-align: center">Weight</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Repetitions</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Sets</th> <th class="cell100 column4" style="width:16.6%; text-align: center">Max Rep weight</th> <th class="cell100 column5" style="width:16.6%; text-align: center">Pause</th> </tr> </thead> </table> </div> <div class="table100-body js-pscroll" id = "test"> <table> <tbody id="myTable1"> <tr class="row100 body"> </tbody> </table> </div> </div> </div> <button onclick="addRow(\'myTable1\')" class="button">Add new Exercise</button> <button onclick="deleteRow(\'myTable1\')" class="button">Remove last Exercise</button> <button onclick="deleteTable(\'1\')" class="button">Delete Day</button></div></div>';
        var basicTable2 = '<div class="limiter" id="day2"> <div class="container-table100"> <div class="wrap-table100"> <div class="table100 ver3 m-b-110"> <div class="table100-head"> <table> <thead> <tr class="row100 head"> <th class="cell100 column1" style="width:16.6%; text-align: center">Exercise</th> <th class="cell100 column2" style="width:16.6%; text-align: center">Weight</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Repetitions</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Sets</th> <th class="cell100 column4" style="width:16.6%; text-align: center">Max Rep weight</th> <th class="cell100 column5" style="width:16.6%; text-align: center">Pause</th> </tr> </thead> </table> </div> <div class="table100-body js-pscroll" id = "test"> <table> <tbody id="myTable2"> <tr class="row100 body"> </tbody> </table> </div> </div> </div> <button onclick="addRow(\'myTable2\')" class="button">Add new Exercise</button> <button onclick="deleteRow(\'myTable2\')" class="button">Remove last Exercise</button> <button onclick="deleteTable(\'2\')" class="button">Delete Day</button></div> </div>';
        var basicTable3 = '<div class="limiter" id="day3"> <div class="container-table100"> <div class="wrap-table100"> <div class="table100 ver3 m-b-110"> <div class="table100-head"> <table> <thead> <tr class="row100 head"> <th class="cell100 column1" style="width:16.6%; text-align: center">Exercise</th> <th class="cell100 column2" style="width:16.6%; text-align: center">Weight</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Repetitions</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Sets</th> <th class="cell100 column4" style="width:16.6%; text-align: center">Max Rep weight</th> <th class="cell100 column5" style="width:16.6%; text-align: center">Pause</th> </tr> </thead> </table> </div> <div class="table100-body js-pscroll" id = "test"> <table> <tbody id="myTable3"> <tr class="row100 body"> </tbody> </table> </div> </div> </div> <button onclick="addRow(\'myTable3\')" class="button">Add new Exercise</button> <button onclick="deleteRow(\'myTable3\')" class="button">Remove last Exercise</button> <button onclick="deleteTable(\'3\')" class="button">Delete Day</button></div> </div>';
        var basicTable4 = '<div class="limiter" id="day4"> <div class="container-table100"> <div class="wrap-table100"> <div class="table100 ver3 m-b-110"> <div class="table100-head"> <table> <thead> <tr class="row100 head"> <th class="cell100 column1" style="width:16.6%; text-align: center">Exercise</th> <th class="cell100 column2" style="width:16.6%; text-align: center">Weight</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Repetitions</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Sets</th> <th class="cell100 column4" style="width:16.6%; text-align: center">Max Rep weight</th> <th class="cell100 column5" style="width:16.6%; text-align: center">Pause</th> </tr> </thead> </table> </div> <div class="table100-body js-pscroll" id = "test"> <table> <tbody id="myTable4"> <tr class="row100 body"> </tbody> </table> </div> </div> </div> <button onclick="addRow(\'myTable4\')" class="button">Add new Exercise</button> <button onclick="deleteRow(\'myTable4\')" class="button">Remove last Exercise</button> <button onclick="deleteTable(\'4\')" class="button">Delete Day</button></div> </div>';
        var basicTable5 = '<div class="limiter" id="day5"> <div class="container-table100"> <div class="wrap-table100"> <div class="table100 ver3 m-b-110"> <div class="table100-head"> <table> <thead> <tr class="row100 head"> <th class="cell100 column1" style="width:16.6%; text-align: center">Exercise</th> <th class="cell100 column2" style="width:16.6%; text-align: center">Weight</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Repetitions</th> <th class="cell100 column3" style="width:16.6%; text-align: center">Sets</th> <th class="cell100 column4" style="width:16.6%; text-align: center">Max Rep weight</th> <th class="cell100 column5" style="width:16.6%; text-align: center">Pause</th> </tr> </thead> </table> </div> <div class="table100-body js-pscroll" id = "test"> <table> <tbody id="myTable5"> <tr class="row100 body"> </tbody> </table> </div> </div> </div> <button onclick="addRow(\'myTable5\')" class="button">Add new Exercise</button> <button onclick="deleteRow(\'myTable5\')" class="button">Remove last Exercise</button> <button onclick="deleteTable(\'5\')" class="button">Delete Day</button></div> </div>';


        var id = numtables + 1;			// number of tables
        numtables = numtables + 1;
        var tablename = 'container';
        var temptablename = tablename.concat(id);
        var tmptabletocreate = 'basicTable';
        var tabletocreate = tmptabletocreate.concat(id);

        /*
		console.log(numtables);
        console.log(id);
        console.log(tablename);
        console.log(temptablename);
		console.log(tabletocreate);
		*/

        if(id == 1)
        {
            document.getElementById(temptablename).innerHTML = basicTable1;

            if(addRowByDefault)
                addRow("myTable1");
        }
        else if(id == 2)
        {
            document.getElementById(temptablename).innerHTML = basicTable2;

            if(addRowByDefault)
                addRow("myTable2");
        }
        else if(id == 3)
        {
            document.getElementById(temptablename).innerHTML = basicTable3;

            if(addRowByDefault)
                addRow("myTable3");
        }
        else if(id == 4)
        {
            document.getElementById(temptablename).innerHTML = basicTable4;

            if(addRowByDefault)
                addRow("myTable4");
        }
        else if(id == 5)
        {
            document.getElementById(temptablename).innerHTML = basicTable5;

            if(addRowByDefault)
                addRow("myTable5");
        }
    }

    function addRow(tableName)
    {
        var table = document.getElementById(tableName);
        var rowCount = document.getElementById(tableName).rows.length;

        // console.log(rowCount);

        var row = table.insertRow(rowCount);
        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        var cell4 = row.insertCell(3);
        var cell5 = row.insertCell(4);
        var cell6 = row.insertCell(5);

        cell1.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='name' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/></div></td>";
        cell2.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='kilo' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/></div></td>";
        cell3.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='reps' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/></div></td>";
        cell4.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='sets' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/></div></td>";
        cell5.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='maxs' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/></div></td>";
        cell6.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='time' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/></div></td>";

        row.setAttribute("id", "somerow", 0);
    }

    function deleteRow(tableName)
    {
        var x = document.getElementById(tableName).rows.length;
        document.getElementById(tableName).deleteRow(x - 1);
    }

    function deleteTable(id)
    {
        document.getElementById("day" + id).innerHTML = ' ';
        console.log('deleted: day' + id);

        for(var i = id; i <= numtables; i++)    // start at the position where the table has been deleted, no need to change id of earlier tables
        {
            // dont set the first day as the zeroest day - doesnt exist
            // dont reset the table i wanted to delete
            // if the last day has changed dont set all the others back
            if(document.getElementById('day' + i).id != 'day1' && i != id && id != numtables)
            {
                document.getElementById("day" + i).id = 'day' + (i - 1);
                document.getElementById("myTable" + i).id = 'myTable' + (i - 1);

                console.log('day' + i + ' will become: day' + (i - 1))
            }
        }

        numtables = numtables - 1;
    }

    function addRowWithEx(tableName, exname, exweight, exreps, exsets, exmaxreps, expause)
    {
        // console.log(tableName);

        var table = document.getElementById(tableName);
        var rowCount = document.getElementById(tableName).rows.length;

        // console.log(rowCount);

        var row = table.insertRow(rowCount);
        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        var cell4 = row.insertCell(3);
        var cell5 = row.insertCell(4);
        var cell6 = row.insertCell(5);

        cell1.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='name' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/>" + exname + "</div></td>";
        cell2.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='kilo' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/>" + exweight + "</div></td>";
        cell3.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='reps' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/>" + exreps + "</div></td>";
        cell4.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='sets' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/>" + exsets + "</div></td>";
        cell5.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='maxs' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/>" + exmaxreps + "</div></td>";
        cell6.innerHTML = "<td class=\'tdinput\'> <div contenteditable='true' data-placeholder='time' class='title' style=\"background-color: transparent; color:#7F7F7F; width:100px; text-align: center\"/>" + expause + "</div></td>";

        row.setAttribute("id", "somerow", 0);
    }

    function doesPlanNameExists(plannamelocal)
    {
        var request = new XMLHttpRequest();
        var url = 'http://jakob.ml:50003/trainingsplan/getPlansByUser/';
        var plan = '';
        var username = getusername();

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open("GET", url + username, false);     // false for synchronous request
        xmlHttp.send(null);
        plans = xmlHttp.responseText;


        var json = JSON.parse(plans);

        var amountdays = (plans.match(/name/g) || []).length;

        for(var i = 0; i < amountdays - 1; i++)
        {
            if(plannamelocal == json.plans[i].name)
            {
                alert("A plan with the same name already exists, please choose another one!");
                return true;
            }
        }
    }

    function getExercises()
    {
        var defaulttablename = 'myTable';
        var finaltablename;
        var row = document.getElementById("somerow");
        var filteredText = '{"plan":[';
        var localplanid;

        filteredText = filteredText + '{"username": "' + getusername() + '",';

        if(planname.length > 0 && currentPlanId.length == 0)
        {
            localplanid = getPlanID();

            if(localplanid != -1)
                currentPlanId = localplanid;
        }
            filteredText = filteredText + '"planid": "' + currentPlanId + '",';


        if(planname.length == 0)        // new plan, not updated plan
        {
            plannamelocal = EnterPlanName();

            planname = plannamelocal;

            document.getElementById("username").innerHTML = planname + ' | '+ getusername() + ' ' + '<span class="glyphicon">&#xe163;</span>';


            if(doesPlanNameExists(plannamelocal))
            {
                return 'ERRORCODE 00004';
            }
            filteredText = filteredText + '"planname": "' + plannamelocal + '",';
        }
        else
            filteredText = filteredText + '"planname": "' + planname + '",';

        if(filteredText.includes('ERRORCODE 00003'))
        {
            return 'ERRORCODE 00003';
        }

        for(var k = 1; k <= numtables; k++)		// For multiple Tables, num of table start at 1 not 0
        {
            finaltablename = defaulttablename.concat(k);
            //console.log(finaltablename);

            var table = document.getElementById(finaltablename);
            var rowlength = table.getElementsByTagName("tr").length - 1;

            if(k == 1)
                filteredText = filteredText + '"day":[';
            else
                filteredText = filteredText + '{"day":[';

            for(var j = 1; j <= rowlength; j++)
            {

                filteredText = filteredText + '{"exercise":{'

                //	filteredText = filteredText + '{"exercise": {'

                var rawText = document.getElementById(finaltablename).rows[j].innerHTML;
                filteredText = filteredText + parse(rawText);

                if(j == rowlength)
                    filteredText = filteredText + '}';
                else
                    filteredText = filteredText + '},';
            }

            if(k < numtables)
                filteredText = filteredText + ']},';
            else // if(k == numtables)
                filteredText = filteredText + ']}';


            if(k < numtables)
                filteredText = filteredText + '';
            else // if(k == numtables)
                filteredText = filteredText + ']}';
        }

        if(!filteredText.includes('day'))
            filteredText = '{"plan":[{"username": "NoUser","planname": "NoPlan"}]}';

        // console.log(filteredText);

        return filteredText;
    }

    function parse(raw)
    {
        var parsed = '';
        var res = raw.split("</td>")

        for(var i = 0; i < 6; i++)
        {
            if (i == 0)
                parsed = parsed + "\"name\":\"" + res[i].substring(raw.indexOf('center">') + 8, res[i].indexOf('</div>')) + '",';
            else if(i == 1)
                parsed = parsed + "\"weight\":\"" + res[i].substring(raw.indexOf('center">') + 8, res[i].indexOf('</div>')) + '",';
            else if(i == 2)
                parsed = parsed + "\"reps\":\"" + res[i].substring(raw.indexOf('center">') + 8, res[i].indexOf('</div>')) + '",';
            else if(i == 3)
                parsed = parsed + "\"sets\":\"" + res[i].substring(raw.indexOf('center">') + 8, res[i].indexOf('</div>')) + '",';
            else if(i == 4)
                parsed = parsed + "\"maxrep\":\"" + res[i].substring(raw.indexOf('center">') + 8, res[i].indexOf('</div>')) + '",';
            else if(i == 5)
                parsed = parsed + "\"pause\":\"" + res[i].substring(raw.indexOf('center">') + 8, res[i].indexOf('</div>')) + '"}';

            // parsed = parsed + res[i].substring(raw.indexOf('center">') + 8, res[i].indexOf('</div>')) + ';';
        }

        return parsed;
    }

    function loadPlanIntoDB()
    {
        var jsondata = getExercises();
        var xhr = new XMLHttpRequest();
        var url = "http://jakob.ml:50003/trainingsplan/sendPlan";
        xhr.open("POST", url, true);
        xhr.setRequestHeader("Content-Type", "text/plain");

        var data = JSON.stringify(jsondata);
        if(jsondata == 'ERRORCODE 00003' || jsondata == 'ERRORCODE 00004')
        {
            console.log("sending dummy plan");
            console.log('{"plan":[{"username": "NoUser","planname": "NoPlan"}]}');
            xhr.send('{"plan":[{"username": "NoUser","planname": "NoPlan"}]}');
        }
        else
        {
            console.log("no error found - sending normal plan");
            console.log(jsondata);
            xhr.send(jsondata);


            alert("Plan - " + planname +  " successfully Saved!");

        }
    }

    /*
    function receivePlanfromDB()
    {
        var request = new XMLHttpRequest();

        request.open('GET', 'http://jakob.ml:50003/trainingsplan/sendjson', true);
        request.onload = function ()
        {
            // Begin accessing JSON data here
            var data = JSON.parse(this.response);

            if (request.status >= 200 && request.status < 400)
            {
                console.log(data);

            }
            else
            {
                console.log('error');
            }
        }
        request.send();
    }
    */

    function receivePlanfromDB(onlogon)
    {
        var request = new XMLHttpRequest();

        var url = 'http://jakob.ml:50003/trainingsplan/getPlansByUser/';
        var username = getusername();
        var planid;

        var plan;

        request.open('GET', url + username, true);
        request.onload = function ()
        {
            // Begin accessing JSON data here
            if (request.status >= 200 && request.status < 400)
            {
                if((this.response.match(/name/g) || []).length > 2 && onlogon == false) // if more than 1 day (2 because name is also in username) than load all plans
                {
                    document.getElementById('dialog').style.display = 'inline';
                    showDialog('http://jakob.ml:50003/trainingsplan/getPlansByUser/');
                    getPlanIdFromDialog(username);
                }
                else
                {
                    plan = getPlan('http://jakob.ml:50003/trainingsplan/getNewestPlan/' + username);
                    // console.log(plan);
                    var data = JSON.parse(plan);
                    printjson(data, plan);
                }
            }
            else
            {
                console.log('error');
            }
        }

        request.send();

    }

    function getPlan(urlToCall)
    {
        var request = new XMLHttpRequest();
        var url = urlToCall;
        var plan = '';
        var username = getusername();

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", url, false ); // false for synchronous request
        xmlHttp.send( null );
        plan = xmlHttp.responseText;


        console.log(plan);
        return plan;
    }

    function getPlanID()
    {
        var request = new XMLHttpRequest();
        var url = 'http://jakob.ml:50003/trainingsplan/getPlanIdByUsernameAndPlanname/';
        var username = getusername();

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", url + username + '/' + planname, false ); // false for synchronous request
        xmlHttp.send( null );
        plan = xmlHttp.responseText;

        return plan;
    }

    function showDialog(urlToCall)
    {
        var confirmButton = document.getElementById('confirm');
        var request = new XMLHttpRequest();
        var url = urlToCall;

        var username = getusername();

        request.open('GET', url + username, true);
        request.onload = function ()
        {
            // Begin accessing JSON data here
            if (request.status >= 200 && request.status < 400)
            {
                var json = JSON.parse(this.response);

                // console.log(json["username"]);

                var amountdays = (this.response.match(/name/g) || []).length;

                /*
                var select = document.getElementById("test-dropdown");
                var length = select.options.length;

                for (var i = 0; i < length; i++)
                {
                    select.options[i] = null;
                }
                */

                for(var i = 0; i < amountdays - 1; i++)
                {
                    /*
                    console.log(json.plans[i].name);
                    console.log(json.plans[i].date);
                    console.log(json.plans[i].id);
                    */

                    daySelect = document.getElementById('test-dropdown');

                    // console.log(loaded);

                    if(!loaded)
                    {
                        daySelect.options[daySelect.options.length] = new Option(json.plans[i].date + ' - ' +  json.plans[i].name, json.plans[i].id);
                    }
                }

                loaded = true;      // loaded is true - which means all plans are loaded and dont add them AGAIN to dialog
            }
            else
            {
                console.log('error');
            }
        }

        request.send();
    }

    function getPlanIdFromDialog(username)
    {
        var confirmButton = document.getElementById('confirm');
        var cancelButton = document.getElementById('cancel');

        confirmButton.addEventListener('click', function()
        {
            var x = document.getElementById('test-dropdown');
            // console.log(x.options[x.selectedIndex].value);

            currentPlanId = x.options[x.selectedIndex].value;

            // TODO value at the end of url can probably be removed
            plan = getPlan('http://jakob.ml:50003/trainingsplan/getPlanByUserAndPlan/' + username + '/' + x.options[x.selectedIndex].value);
            var data = JSON.parse(plan);

            printjson(data, plan);
            document.getElementById('dialog').style.display = 'none';
            // return x.options[x.selectedIndex].value;
        });

        cancelButton.addEventListener('click', function()
        {
            console.log("CLOSED");
            document.getElementById('dialog').style.display = 'none';
        });
    }

    function printjson(json, plaintext)
    {
        var daycount = (plaintext.match(/day/g) || []).length;

        // console.log(json);

        cleanUpTables();

        for(var i = 0; i < daycount; i++)
        {
            var str = JSON.stringify(json.plan[i]);
            var excount = (str.match(/exercise/g) || []).length;

            var tablenum = i + 1;
            var table = 'myTable' + tablenum;

            addTable(false);

            if(i == 0)
            {
                document.getElementById("username").innerHTML = json.plan[i].planname + ' | '+ getusername() + ' ' + '<span class="glyphicon">&#xe163;</span>';
                planname = json.plan[i].planname;
            }

            if(i == 0)
                currentPlanId = json.plan[i].planid;

            console.log(currentPlanId);

            for(var j = 0; j < excount; j++)
            {
                addRowWithEx(table, json.plan[i].day[j].exercise.name
                    , json.plan[i].day[j].exercise.weight
                    , json.plan[i].day[j].exercise.reps
                    , json.plan[i].day[j].exercise.sets
                    , json.plan[i].day[j].exercise.maxrep
                    , json.plan[i].day[j].exercise.pause);
            }
        }
    }

    function EnterPlanName()
    {
        var txt;
        var planname = prompt("Please enter the name of your plan:", "Plan by ..." /*+ getusername()*/ );

        if (planname == null || planname == "")
        {
            return "ERRORCODE 00003 - User cancelled the prompt.";
        }
        else
        {
            return planname;
        }
    }

    function cleanUpTables()
    {
        document.getElementById("username").innerHTML = getusername() + ' ' + '<span class="glyphicon">&#xe163;</span>';
        planname = '';

        for(var i = 1; i < 6; i++)
        {
            var day_name = 'container' + i;
            var element =  document.getElementById(day_name);

            // console.log(day_name);

            resetDays(day_name);
        }
    }

    function resetDays(day_name)
    {
        var element =  document.getElementById(day_name);

        if (typeof(element) != 'undefined' && element != null)
        {
            var day = document.getElementById(day_name);
            day.innerHTML = '';
            numtables = 0;
        }

        currentPlanId = '';
    }

    document.addEventListener('DOMContentLoaded', function()
    {
        var check = function()
        {
            if(getusername().length > 0){
                receivePlanfromDB(true);
            }
            else {
                setTimeout(check, 1000); // check again in a second
            }
        }

        check();

    }, false);


</script>
<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
<script src="vendor/bootstrap/js/popper.js"></script>
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<script src="vendor/select2/select2.min.js"></script>
<script src="vendor/perfect-scrollbar/perfect-scrollbar.min.js"></script>
<script>
    $('.js-pscroll').each(function(){
        var ps = new PerfectScrollbar(this);

        $(window).on('resize', function(){
            ps.update();
        })
    });
</script>
<script src="js/main.js"></script>
<script src="http://jakob.ml:8081/auth/js/keycloak.js"></script>
<script>
    var username = '';
    var kc = Keycloak({
        url: 'http://jakob.ml:8081/auth',
        realm: 'trainingsplan',
        clientId: 'trainingsplan'
    });

    kc.init({onLoad: 'check-sso', checkLoginIframe: false }).success(function () {            // login-required - check-sso
        username = kc.idTokenParsed.name
        document.getElementById("username").innerHTML = username + '   ' + '<span class="glyphicon">&#xe163;</span>' ;
    }).error(function () {
        // window.location.reload();
    });

    function getusername()
    {
        console.log(username);
        return username;
    }

    function logout()
    {
        window.location.href = "http://jakob.ml:8081/auth/realms/trainingsplan/protocol/openid-connect/logout?redirect_uri=" +
            "/auth/realms/trainingsplan/protocol/openid-connect/auth?response_type=code&client_id=trainingsplan&redirect_uri=http%3A%2F%2Fjakob.ml%3A50001%2Ftrainingsplan%2F&state=d6f2b29a-cd94-4d93-b1d0-ff9750b4e19c&login=true&scope=openid";
    }
</script>
</body>
</html>