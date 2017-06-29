This source code is hosted at 

Follow the instructions to install gradle here - https://gradle.org/install

# issue pull request
>> git pull https://github.com/sushiljacksparrow/xeneta.git

# start server
>> ./gradlew bootRun

# curl GET /rates
>> curl 'http://localhost:8080/rates?date_from=2016-01-03&date_to=2016-01-30&origin=CNGGZ&destination=EETLL'

# post rates

>> curl -v -H "Content-Type: application/json" -X POST --data "@try.json" localhost:8080/rates

try.json file would look like -- 

>> cat try.json
{
  "fromDate": "2017-06-28",
    "toDate": "2017-06-30",
    "origin": "DKCPH",
    "destination": "CNSNZ",
    "value": "300",
    "currency": "INR"
}


