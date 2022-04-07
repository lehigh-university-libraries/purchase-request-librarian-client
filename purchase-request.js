// ==UserScript==
// @name         Submit Library Purchase Request
// @namespace    http://library.lehigh.edu/
// @version      0.1
// @description  Submit the item on the current page as a library purchase request.
// @author       Maccabee Levine
// @match        https://www.amazon.com/*/dp/*
// @icon         https://library.lehigh.edu/sites/library.lehigh.edu/themes/library2013/favicon.ico
// @require      https://code.jquery.com/jquery-3.6.0.min.js
// @connect      lehigh.edu
// @connect      localhost
// @grant        GM_addStyle
// @grant        GM_getValue
// @grant        GM_setValue
// @grant        GM_xmlhttpRequest
// ==/UserScript==

/* global $ */ // This is for the linter, to eliminate warnings about $ being undefined.

$(document).ready(function () {
    checkFirstRun();

    initStyles();

    // add button to page
    let lehighButton = $("<button class='lehigh-button'><img src='https://library.lehigh.edu/sites/library.lehigh.edu/themes/library2013/favicon.ico'> Lehigh: Request Purchase</button>");
    let lehighSection = $("<div>").addClass("a-section").append(lehighButton);
    lehighButton.on("click", submitRequest);
    $("#rightCol").prepend(lehighSection);
});

function submitRequest() {
    let title = trim($("#productTitle").text());
    let contributor = trim($(".contributorNameID").text());
    let isbn = trim($(".prodDetSectionEntry:contains(ISBN-10)").next(".prodDetAttrValue").text());
    let username = GM_getValue("username");
    let data = {
        "title": title,
        "contributor": contributor,
        "isbn": isbn,
        "requesterUsername": username
    };
    console.log("data: ", data);
    GM_xmlhttpRequest({
        method: "POST",
        url: GM_getValue("url"),
        user: username,
        password: GM_getValue("password"),
        headers: {
            "Content-Type": "application/json",
        },
        data: JSON.stringify(data),
        onerror: function (event) {
            console.log("error submitting request: ", event);
            alert("Failed to submit request; browser error.");
        },
        onload: function (result) {
            console.log("result: ", result);
            if (result.status == 201) {
                alert("Submitted purchase request for " + title);
            }
            else {
                alert("Failed to submit request; status: " + result.status);
            }
        }
    });
}

function trim(input) {
    if (input == null || input.length == 0) {
        return null;
    }
    return input.trim();
}

function checkFirstRun() {
    let username = GM_getValue("username");
    if (username == null) {
        username = prompt("Username for Lehigh Purchase Requests");
        let password = prompt("Password for Lehigh Purchase Requests");
        let url = prompt("URL for Lehigh Purchase Requests");
        GM_setValue("username", username);
        GM_setValue("password", password);
        GM_setValue("url", url);
    }
}

function initStyles() {
    let styles = `
        .lehigh-button {
             padding: 10px;
        }
    `;
    GM_addStyle(styles);
}
