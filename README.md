# Trainingsplan

## Infrastructure ##

* JAVA BACKEND <br/>
* Frontend designed with html, css3 and js<br/>
* SECURED WITH KEYCLOAK<br/>
* USING VISUAL STUDIO CODE<br/>
* FRONTEND SERVED BY NGINX<br/>
* RUNNING ON DOCKER<br/>

<div style="display:table-cell; vertical-align:middle; text-align:center">
	<p>
 	<img src="https://cdn-images-1.medium.com/max/2600/1*l4xICbIIYlz1OTymWCoUTw.jpeg" width="140" title="hover text">
  	<img src="https://www.cbronline.com/wp-content/uploads/2016/06/Java.png" width="140" title="hover text">
	<img src="https://cdn-images-1.medium.com/max/1600/1*fK7U56Q-MRzEkuWHmMRn1Q.png" width="140" title="hover text">
	<img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtQSGh0yCrRRhfzku93O-RQeTF7Ey3WEB60cFUlUVxqSwEWF_A" width="140" title="hover text">
	<img src="https://assets.t3n.sc/news/wp-content/uploads/2016/03/nginx-hoster-featured-620x349.jpg?auto=format&h=349&ixlib=php-2.1.1&w=620" width="140" title="hover text">
		<img src="https://www.docker.com/sites/default/files/social/docker_facebook_share.png" width="140" title="hover text">
	</p>
</div>

## Screenshots ##
Data Input
<img src="https://imgur.com/oOLGYnu" width="140" title="hover text">

Stats

<img src="https://imgur.com/O0G6eGP" width="140" title="hover text">
<img src="https://imgur.com/MocvfjR" width="140" title="hover text">
- - - -
## Get access to stats ##

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
