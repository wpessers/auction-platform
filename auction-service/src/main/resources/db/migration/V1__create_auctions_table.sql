CREATE TABLE auctions
(
  id             UUID           NOT NULL,
  name           VARCHAR(100)   NOT NULL,
  description    VARCHAR(400),
  start_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_time       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  starting_price DECIMAL(10, 2) NOT NULL,
  status         VARCHAR(255)   NOT NULL,
  CONSTRAINT pk_auctions PRIMARY KEY (id)
);