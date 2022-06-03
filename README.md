# Purchase Request Librarian Client

The Librarian client is a browser plug-in (a [TamperMonkey](https://chrome.google.com/webstore/detail/tampermonkey/dhdgffkkebhmkfjojejmpbldmpobfkfo?hl=en) script) that allows librarians to submit purchase requests to the [Purchase Request Workflow Proxy Server](https://github.com/lehigh-university-libraries/purchase-request-workflow-proxy-server). The client:

* Activates on any book item page on Amazon.com.
* Adds a button to the page that allows the librarian to request that the library purchase the item.
* Collects some optional preferences from the librarian including print vs. electronic and what to do after the item is purchased.
* Submits purchase requests for newly identified items to the [Purchase Request Workflow Proxy Server](https://github.com/lehigh-university-libraries/purchase-request-workflow-proxy-server) for processing by acquisitions staff.
    * The Workflow Proxy Server centralizes purchase requests from different sources, enriches them with information to help selector decision-making, and routes them to external systems for post-decision processing.

## Why Amazon?

Amazon item pages are used only as a pass-through to direct purchase requests to the library.  The advantages are:

- Amazon book pages almost always have an ISBN.  This is a useful bit of metadata for the rest of the purchase request process, and so is sent to the server along with the title and author.
- Librarians can install this [additional browser extension](https://chrome.google.com/webstore/detail/amazopen-right-click-sear/ehhhlpdgplkjdcgodmkgonnjhpkdiilc) that adds a right-click menu to link from any selected text to perform a title search on Amazon.  Thus, librarians can link from any website that has a book title (reviews, recommendations, etc.) to Amazon, and from there submit a purchase request to the library.
- Some librarians report finding the additional data on the Amazon page useful.

## Deployment

The Lost Items Client combines a TamperMonkey script and a Java Spring Boot application.  

* The script `purchase-request.user.js` should be hosted on a local web server.  When a browser with TamperMonkey installed accesses the URL, it will recognize the script and prompt the user to install it.

* The Spring Boot server application is a secure pass-through to relay the requests to the Workflow Proxy Server.  Like that server, it can be deployed either as a standalone application or embedded in a Java application server.  Follow the [deployment instructions for the Workflow Proxy Server](https://github.com/lehigh-university-libraries/purchase-request-workflow-proxy-server#deployment).  

## Dependencies

### Browser

- [TamperMonkey](https://chrome.google.com/webstore/detail/tampermonkey/dhdgffkkebhmkfjojejmpbldmpobfkfo?hl=en) browser extension
- Optional: [Amazopen](https://chrome.google.com/webstore/detail/amazopen-right-click-sear/ehhhlpdgplkjdcgodmkgonnjhpkdiilc) browser extension

### Server

- Java SE.  Tested on Java SE 11 (LTE).
- Workflow Proxy Server

## Initial Setup

1. Create a Workflow Proxy Server user for API access by the Librarian Client.  See [Client User Management](https://github.com/lehigh-university-libraries/purchase-request-workflow-proxy-server#client-user-management).

1. Set up the [configuration file](#configuration).

1. Uncomment this property to the configuration file before starting the application is run for the first time, to create the database schema.  *Then re-comment or remove it*:

    \# spring.jpa.hibernate.ddl-auto=create-drop

## User Management

Librarian Client users submit purchase requests to the server along with credentials stored in the client user script (and set when it is first installed).  These are stored (encrypted) in the [database](#database-section).

Scripts are available to add and remove user credentials.  They utilize the same `purchase-request-librarian-client.war` built via Maven, and the same [configuration file](#configuration).

### Add Client Credentials

In the project home directory, run:

`scripts/add_user.sh username password`

### Delete Client Credentials

In the project home directory, run:

`scripts/delete_user.sh username`

## Configuration

Copy/rename `application.properties.example` to `application.properties` and configure its parameters.  See example values in that file.

| Property | Description | Required |
| -- | -- | -- |
| librarian-client.enabled | Enable the application. Must be 'true'.  | Y |

### Database Section

A linked MySQL database.  The database is used only for local authentication credential storage.

| Property | Description | Required |
| -- | -- | -- |
| librarian-client.db.host | Database hostname | Y |
| librarian-client.db.name | Database schema name | Y |
| librarian-client.db.username | Database username | Y |
| librarian-client.db.password | Database password | Y |

### Workflow Proxy Server Section

For connecting to the Purchase Request Workflow Proxy Server via its API.

| Property | Description | Required |
| -- | -- | -- |
| librarian-client.workflow-server.username | API username | Y |
| librarian-client.workflow-server.password | API password | Y |
| librarian-client.workflow-server.base-url | API base URL | Y |

### Debugging

Optional properties.  See other [Spring Boot logging properties](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging) as well.

| Property | Description | Required |
| -- | -- | -- |
| logging.level.edu.lehigh.libraries.purchase_request | To optionally change the default [SLF4J](https://www.slf4j.org/index.html) logging [level](https://www.slf4j.org/api/org/slf4j/event/Level.html#enum.constant.summary) from INFO. `DEBUG`, `ERROR`, etc. | N |
| logging.file.name | Optionally [output the log to a file](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging.file-output). | N |
