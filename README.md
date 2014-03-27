Simple Remote Service
=====================

How To Run
----------

To run the service just issue the following command:

> sbt service/run

Service will be started on port 9000 listening to all network interfaces.


Service API
-----------

Submit a job for processing:

POST /api/service
Accept: application/json
Body: <Job>

Request Body example:

      {
        "id": "job-1",
        "lines": [
           "This a line",
           "And that's another line."
        ]
      }

Response Body example:

     {
        "id": "job-1",
        "lines": [
            "62812ce276aa9819a2e272f94124d5a1",
            "13ea8b769685089ba2bed4a665a61fde"
        ]
     }
