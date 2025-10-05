# Auction Platform Demo Project
This project is a sample application to demonstrate some Spring Boot basics,
as well as explore hexagonal architecture combined with DDD.
It is not intended for production use, but more as a learning resource for myself.
The project will grow over time to incorporate more features / patterns,
as I get free time to work on it. Currently, it is far from finished.

## The project
The idea behind this project is to build some sort of auction platform for real time bidding.
Users can create accounts, manage auctions and bid on active auctions. There is also a secondary
microservice, the Pricing Service, which can set a starting price if a user has not provided
one themselves.
*The pricing service has not yet been implemented and was just meant to be used to demonstrate grpc communication between to services*
