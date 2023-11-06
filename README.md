# TinyURL_Backend

## Technologies used:
For this backend, I used technologies such as:
- Java servlet for handling the incoming requests
- MongoDB
- Tomcat8
- Amazon EC2
- Amazon Auto Scaling
- Amazon Elastic Application Load Balancer
- and finally, Amazon S3.

The overall working of the project is explained below in the implementation section.

## Architecture:
The general architecture is shown below:

![image](https://github.com/PV1shal/TinyURL_Backend/assets/113155188/6d4bc298-dc54-46c0-bfda-9b973c619677)

  - Java servlet is used in the backend to handle all the incoming requests. This servlet was later deployed in an AWS EC2 instance.
  - I decided to use MongoDB as my database due to its high scalability and response speed. This database is also running in another EC2 instance.
  - I use AWS Auto Scaling so that the server EC2 instances can be created and terminated based on the traffic.
  - The user-data script was added to the auto-scaler so the EC2 instances can initialize the environment such as downloading Tomcat, downloading the Servlet WAR code from AWS S3, and deploying it into Tomcat.
  - All these EC2 instances are load-balanced by an Amazon Elastic Application Load Balancer for high throughput.
  - The throughput can be even further increased by providing a cache such as Redis and also having multiple Databases.
    
## Implementation:
- The overall backend was developed using Java Servlet to handle the incoming requests. The URI for the backend should look something like `http://<hostname>/<path>`
- The servlet can handle the has the following endpoints/paths to fulfill all the requirements of the assessment:
    - <b>`/*`</b>:
        - The user can enter the tiny URL path here.
        - Servlet will redirect the user to the long URI website.
        - If a tiny URL doesn't exist in the database, status code 404 is sent back to the user with a response message stating, "URI does not exist".
    - <b>`/history?username=<username>`</b>:
        - This endpoint sends a list of long URLs and the corresponding tiny URLs created by a user back to the client.
        - The username is sent to the backend as a parameter.
    - <b>`/maketiny`</b>:
        - This endpoint is used to create a tiny URL instead of the long URL.
        - This sends the following information in the body section of the request so it can be saved into the Database:
            - url: the long URI which needs to be converted into a tiny URI
            - username: the user who wants to create the URI. In the following section, it will be explained why the username is used.
            - custom: a bool that will let the server know if the client wants a randomly generated tiny URI or not.
            - customtiny: This is an optional key that is needed if the client wants to create a custom tiny URI.
        - The Servlet will take all the data from the request and create a tiny URI.
        - If custom is set to true, the servlet will check if the custom URL that the client wants exists in the database or not.
            - If it does exist, a response is sent back to the user stating that the custom URL is not available.
            - If it does not exist, the custom URI, long URI, and Username are saved in the database.
            - The number of requests is deducted from the user.
  - <b>`/user`</b>:
      - This endpoint is used to create users.
      - The user data is sent as a request body to this endpoint. The body should hold the following details:
          - username: This is the username of the client.
          - password: This is the password of the client that could be used to login.
          - Tier: The Tier of the user that determines the number of requests the user can use to create a tiny URL.
      - The server sets the number of requests the user has based on the tier.
      - There are 2 tiers:
          - Tier 1 has 1000 requests.
          - Tier 2 has 100 requests.
       
## How it works:
  - I use MongoDB as the database as it is very scalable and fast compared to relation databases. It contains 2 collections which I used:
      - users: Collection of user credentials.
      - paths: Collection of tiny URLs created by a user and the long URL and also the user who created it which can be used to retrieve the history. 
  - First, the user has to create an account to set the number of requests the user can use to create a tiny URL and store it in the paths collection. After creating a tiny url the number of requests are deducted from the user and is updated into the user collections.
  - The user can create a tiny URL by giving a custom URL or not and if he gives a custom URL it is checked if the custom URL already exists or not in the database.
  - The user can view the history of tiny URLs created by a user using the `/history` path. The server will get the param from the requests and retrieve only the documents with that username.
