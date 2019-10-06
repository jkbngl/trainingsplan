# TI - Trainingsplan Intelligence

## Mission
Bringing **digitalisation** and **innovation** into the GYM, to help you to get the **best version of yourself**, by using the most expensive and valuable **ressource** of the modern days: **DATA**.

## Design
Green stands for Growth and Health, the goal of **TI** is to make a better and healthier version out of you

## Infrastructure ##

* JAVA BACKEND <br/>
* RUNNING ON WILDFLY <br/>
* FRONTEND DESIGNED WITH HTML, CSS3 AND JS<br/>
* SECURED WITH KEYCLOAK<br/>
* USING VISUAL STUDIO CODE<br/>
* FRONTEND SERVED BY NGINX<br/>
* RUNNING ON DOCKER<br/>

<div style="display:table-cell; vertical-align:middle; text-align:center">
	<p>
 	<img src="https://cdn-images-1.medium.com/max/2600/1*l4xICbIIYlz1OTymWCoUTw.jpeg" width="115" title="hover text">
	<img src="https://www.3pillarglobal.com/wp-content/uploads/2016/03/xwildflyfeatured-300x300.png.pagespeed.ic.zhVGfZt0xY.png" width="115" title="hover text">
  	<img src="https://www.cbronline.com/wp-content/uploads/2016/06/Java.png" width="115" title="hover text">
	<img src="https://cdn-images-1.medium.com/max/1600/1*fK7U56Q-MRzEkuWHmMRn1Q.png" width="115" title="hover text">
	<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtQSGh0yCrRRhfzku93O-RQeTF7Ey3WEB60cFUlUVxqSwEWF_A" width="115" title="hover text">
	<img src="https://assets.t3n.sc/news/wp-content/uploads/2016/03/nginx-hoster-featured-620x349.jpg?auto=format&h=349&ixlib=php-2.1.1&w=620" width="115" title="hover text">
		<img src="https://www.docker.com/sites/default/files/social/docker_facebook_share.png" width="115" title="hover text">
	</p>
</div>

# Screenshots

## Login
![alt text](https://raw.githubusercontent.com/jkbngl/trainingsplan/master/pictures/background_improved.png)

## Data Input
![alt text](https://raw.githubusercontent.com/jkbngl/trainingsplan/master/pictures/oOLGYnu%20-%20Imgur.png)


## Stats
![alt text](https://raw.githubusercontent.com/jkbngl/trainingsplan/master/pictures/new_chart_with_dates.png)
![alt text](https://raw.githubusercontent.com/jkbngl/trainingsplan/master/pictures/new_chart_with_dates_line.png)
![alt text](https://raw.githubusercontent.com/jkbngl/trainingsplan/master/pictures/barchart_with_check_progess_dialog.png)


- - - -
## Get access to stats ##

Contact me to ask for access for educational purposes or buy an access via:
* TODO

## Get access to BodyMass Input-Tool ##

Contact me to ask for access for educational purposes or buy an access via:
* TODO

- - - -

## Usage ##

### Index ###

* TODO


### Stats ###

* TODO


## API ##

Send data in json format with following format as POST to following url: http://jakob.ml:50003/trainingsplan/sendPlan
> @POST
```javascript
{
  "plan": [
    {
      "username": "jakob engl",
      "planname": "chestday",
      "planid": "141",
      "day": [
        {
          "exercise": {
            "id": "defaultvaluetoignore",
            "name": "chest press",
            "weight": "100",
            "reps": "15",
            "sets": "5",
            "maxrep": "100",
            "pause": "2"
          }
        },
        {
          "exercise": {
            "id": "defaultvaluetoignore",
            "name": "bench press",
            "weight": "120",
            "reps": "5",
            "sets": "5",
            "maxrep": "5",
            "pause": "4"
          }
        }
      ]
    },
    {
      "day": [
        {
          "exercise": {
            "id": "defaultvaluetoignore",
            "name": "decline bench press",
            "weight": "90",
            "reps": "10",
            "sets": "5",
            "maxrep": "110",
            "pause": "3"
          }
        }
      ]
    }
  ]
}
```
> @POST

Send the preferences for analyzing exercises into the DB to following url: http://jakob.ml:50003/trainingsplan/send_preferences
```javascript
{
  "username": "jakob engl",
  "check_weight": true,
  "check_reps": true,
  "check_sets": false,
  "check_maxrep": false,
  "mul_weight": "11",
  "mul_reps": "16",
  "mul_sets": "7",
  "mul_maxrep": "18"
}
```
> @GET

Get current plan by a user: http://jakob.ml:50003/trainingsplan/getNewestPlan/ < username >

> @GET

Get all plans by a user: http://jakob.ml:50003/trainingsplan/getPlansByUser/ < username >

> @GET

Get the stats of a exercise: http://jakob.ml:50003/trainingsplan/get_stats/ < base_ex_id >
