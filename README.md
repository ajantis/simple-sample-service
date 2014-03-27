Simple Remote Service
=====================

This simple service is designed to hash strings. That's it: "Hashing-as-a-Service".
It accepts batch requests with a list of strings to hash. It returns a list of hashes of that strings (in the same order).


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

Returns an HTTP 201 (ACCEPTED).

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
     
     
Exaple of error response: 

HTTP 500 (Service Unavailable)

    {
        "error": "Something went wrong!"
    }
     
