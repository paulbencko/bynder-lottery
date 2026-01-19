# Bynder Lottery Solution

## Approach

- Thank you for the opportunity, I had great fun solving this exercise! I tried to model the solution closer to a production service, as I wanted to show that I am comfortable working with microservices and have a good understanding of domain separation

- I tried to focus on the tech aspect of the challenge and not on replicating real-world lotteries, so I went with a simple 5/50, 5/54, 6/40 format without a lucky number. The prizes are calculated as:
	- First Prize -> all numbers match
	- Second Prize -> all but 1 numbers match
	- Third Prize -> all but 2 numbers match

- I implemented a very basic user registration/login system and didn’t add a shopping cart or anything related to money

- I used AI to generate a very basic frontend in order to make testing the solution easier than with Postman calls

- Each service has a migration script that inserts some test data, login with `john@doe.com + password` to access it

- Each morning at 6am `lottery-service` creates 3 lotteries

- To not have to wait for the cron to get triggered, if there are no open lotteries they will get created when `lottery-service` starts

- Each day at midnight `lottery-service` draws the winning numbers for the open lotteries and closes them

- To not have to wait for the cron to get triggered I added an admin endpoint to trigger a manual draw of winning numbers and closing of the lotteries (`POST localhost:8082/admin/draw`)

- Checking bets function can be used without being logged in, for the scenario in which someone purchased a lottery ticket at a phisical lottery and doesn’t have an account

## How to Start and Run the Solution

**Required:** Docker Desktop, Make, Java 24 

```bash
make up         # Creates and starts the services + DB + RabbitMQ in Docker containers

open index.html # Opens the frontend

login           # Using 'john@doe.com' -> 'password'

call localhost:8082/admin/draw (POST) # Manually triggers drawing of winning numbers and closing of the lotteries

make down       # Destroys the containers and attached volumes

make infra-up   # Creates and starts the DB + RabbitMQ in Docker containers, useful when starting the services in debug mode is needed

make infra-down # Destroys the containers and attached volumes

make test       # Runs all tests
```

## Possible Improvements

- An admin dashboard where loteries could be created/closed on the fly
- Better logging with proper use of info/debug/warn/error + correlation ids for tracking an issue across services
- Better JUnit coverage
- Integration tests with Testcontainers
- A retry mechanism + a dead letter queue for events that couldn’t be processed in `ballot-service`
- UUIDs (with UUIDv7 to allow ids to still be indexable)
- Swagger
- Monitoring

## Considerations

- Having the app split into microservices would allow for individual scaling of each microservice as needed

- The most intensive processing happens when a lottery closes and all the ballots need to be updated with their respective prizes. In a real-world scenario where there would be multiple lotteries open for a week, there could be tens of millions of ballots that need to be updated. For this reason I went with an event-based approach where `lottery-service` publishes a `“draw completed”` event, which is then processed by `ballot-service` in batches and by spinning up a new thread for each event that is consumed. Other multithreading approaches could also be used

- If we consider 10 million ballots and a time of around 100ms per a batch of 1000, running on 5-10 threads we could expect the update to finish in less than a few minutes on a single pod, so with multiple pods it could go well under a minute. Eventual consistency would be ok for this task as users would simply see their ballots as `“Pending”` until the update is finished