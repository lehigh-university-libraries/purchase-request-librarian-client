// ==UserScript==
// @name         Submit Library Purchase Request
// @namespace    http://library.lehigh.edu/
// @version      0.6
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
        buildInputDialog();
        addToPage(buildRequestButton());
    }
    else {
        addToPage(buildNoIsbnNote());
    }
});

function addToPage(element) {
    $("#rightCol").prepend( $("<div>").addClass("a-section").append(element) );
}

function buildRequestButton() {
    let openButton = $("<button class='lehigh-button lehigh-input-button'><img class='lehigh-logo' src='https://library.lehigh.edu/sites/library.lehigh.edu/themes/library2013/favicon.ico'>Lehigh: Request Purchase</button>");
    openButton.on("click", openInput);
    let input_container = $("<div class='lehigh-input-container' placeholder='Enter request details.'>").append(openButton);
    return input_container;
}

function buildInputDialog() {
    let dialog = $( `
        <dialog class="lehigh-dialog">
            <form class="lehigh-form" method="dialog">
                <div><img class="lehigh-logo" src='https://library.lehigh.edu/sites/library.lehigh.edu/themes/library2013/favicon.ico'>Lehigh Purchase Request</div>
                <div class="lehigh-format lehigh-radio-button-group">
                    <span>Format:</span>
                    <input type="radio" name="format" id="lehigh-format-electronic" value="electronic"><label for="lehigh-format-electronic">Electronic</label>
                    <input type="radio" name="format" id="lehigh-format-print" value="print"><label for="lehigh-format-print">Print</label>
                </div>
                <div class="lehigh-select-container">
                    <label for="lehigh-status">Send to:</label>
                    <select name="status" id="lehigh-status">
                        <option value="approved">Approved</option>
                        <option value="holding">Holding Tank</option>
                    </select>
                </div>
                <textarea class="lehigh-description" placeholder="Enter desired edition, budget code, any other details"></textarea>
                <input type="submit" class="lehigh-submit-button" value="Submit Lehigh Purchase Request"/>
            </form>
        </dialog>
    `);
    $("body").append(dialog);
    $(".lehigh-form").on("submit", formSubmitted);
}

function formSubmitted(event) {
    event.preventDefault();
    $(".lehigh-dialog").get(0).close();
    submitRequest();
}

function buildNoIsbnNote() {
    return $("<p class='lehigh-button-container'><img src='https://library.lehigh.edu/sites/library.lehigh.edu/themes/library2013/favicon.ico'> Lehigh: No ISBN Found</div>");
}

function hasIsbn() {
    return getIsbnLabel().length > 0;
}

function getIsbnLabel() {
    return $(".prodDetSectionEntry:contains(ISBN-10)").add(".a-text-bold:contains(ISBN-10)");
}

function openInput() {
    $(".lehigh-dialog").get(0).showModal();
}

function submitRequest() {
    let title = trim($("#productTitle").text());
    let contributor = trim($(".contributorNameID").text());
    let isbn = trim(getIsbnLabel().next().text());
    let username = GM_getValue("username");
    let format = trim($(".lehigh-format input:checked").val());
    let speed = $("#lehigh-status option:selected").val();
    let comments = trim($(".lehigh-description").val());
    let data = {
        "title": title,
        "contributor": contributor,
        "isbn": isbn,
        "requesterUsername": username,
        "format": format,
        "speed": speed,
        "requesterComments": comments
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
        .lehigh-dialog {
            border: 1px solid black;
            position: absolute;
            top: 30%;
            left: 30%;
            transform: translate(-50%, -50%);
        }
        .lehigh-dialog::backdrop {
            background: rgba(0, 0, 0, .5);
        }
        .lehigh-form > :first-child {
            font-size: 120%;
        }
        .lehigh-form > *:not(:first-child) {
            margin-top: 1rem;
        }
        .lehigh-radio-button-group span {
            margin-right: 1rem;
        }
        .lehigh-radio-button-group label {
            display: inline;
            margin-left: 0.25rem;
            margin-right: 1rem;
            font-weight: normal;
        }
        .lehigh-radio-button-group input {
            display: inline;
        }
        .lehigh-select-container label {
            margin-right: 1rem;
            display: inline;
            font-weight: normal;
        }
        .lehigh-button {
            padding: 10px;
        }
        .lehigh-logo {
            margin-right: 0.5rem;
        }
    `;
    GM_addStyle(styles);
}
