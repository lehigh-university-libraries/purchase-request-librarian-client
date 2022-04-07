// ==UserScript==
// @name         Submit Library Purchase Request
// @namespace    http://library.lehigh.edu/
// @version      0.4
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
    if (hasIsbn()) {
        addToPage(buildButton());
    }
    else {
        addToPage(buildNoIsbnNote());
    }
});

function addToPage(element) {
    $("#rightCol").prepend( $("<div>").addClass("a-section").append(element) );
}

function buildButton() {
    let button = $("<button class='lehigh-button'><img src='https://library.lehigh.edu/sites/library.lehigh.edu/themes/library2013/favicon.ico'> Lehigh: Request Purchase</button>");
    button.on("click", submitRequest);
    return button;
}

function buildNoIsbnNote() {
    return $("<p class='lehigh-button'><img src='https://library.lehigh.edu/sites/library.lehigh.edu/themes/library2013/favicon.ico'> Lehigh: No ISBN Found</div>");
}

function hasIsbn() {
    return getIsbnLabel().length > 0;
}

function getIsbnLabel() {
    return $(".prodDetSectionEntry:contains(ISBN-10)").add(".a-text-bold:contains(ISBN-10)");
}

function submitRequest() {
    let title = trim($("#productTitle").text());
    let contributor = trim($(".contributorNameID").text());
    let isbn = trim(getIsbnLabel().next().text());
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
    checkKey("username", "Username");
    checkKey("password", "Password");
    checkKey("url", "URL");
}

function checkKey(key, label) {
    let value = GM_getValue(key);
    if (value == null) {
        value = prompt(label + " for Lehigh Purchase Requests");
        GM_setValue(key, value);
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
