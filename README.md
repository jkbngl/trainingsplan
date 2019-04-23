# Trainingsplan

## Infrastructure ##

* JAVA BACKEND <br/>
* SECURED WITH KEYCLOAK<br/>
* USING VISUAL STUDIO CODE<br/>
* FRONTEND SERVED BY NGINX<br/>
* RUNNING ON DOCKER<br/>

<div style="display:table-cell; vertical-align:middle; text-align:center">
	<p>
  	<img src="https://www.cbronline.com/wp-content/uploads/2016/06/Java.png" width="150" title="hover text">
	<img src="https://avatars2.githubusercontent.com/u/4921466?s=400&v=4" width="150" title="hover text">
	<img src="https://avocode.com/static/icons/integrations/visual-studio-code.svg?ver=1" width="150" title="hover text">
		<img src="https://assets.t3n.sc/news/wp-content/uploads/2016/03/nginx-hoster-featured-620x349.jpg?auto=format&h=349&ixlib=php-2.1.1&w=620" 
			width="150" title="hover text">
		<img src="https://www.docker.com/sites/default/files/social/docker_facebook_share.png" width="150" title="hover text">
	</p>
</div>

- - - -
## Get access to stats ##


- - - -

## Usage ##

### Index ###

### Stats ###

## API ##

Send data in json format with following format as POST to following url: http://jakob.ml:50003/trainingsplan/sendPlan

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
