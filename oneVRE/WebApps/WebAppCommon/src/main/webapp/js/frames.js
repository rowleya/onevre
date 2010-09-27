/*
 * @(#)frames.js
 * Created: 07-Jun-2007
 * Version: 1.0
 * Copyright (c) 2007, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Provides functions for handling JavaScript frames
 */

// The old window mouse movement event handler
var pag_oldMouseMove = null;

// The old window mouse up event handler
var pag_oldMouseUp = null;

/**
 * Converts percentage widths into pixel widths
 *
 * @param items - an array of ids of elements to perform the conversion on
 * @param fullWidth - the width to consider as 100%
 */
function pag_setPixelValues(items, fullWidth) {
    for (var i=0; i<items.length; i++)  {
        var item = document.getElementById(items[i]);
        if (item.style.width.charAt(item.style.width.length - 1) == '%') {
            var widthpercent = parseInt(item.style.width);
            item.style.width = ((fullWidth * widthpercent) / 100) + "px";
        }
        if (item.style.left.charAt(item.style.left.length - 1) == '%') {
            var leftpercent = parseInt(item.style.left);
            item.style.left = ((fullWidth * leftpercent) / 100) + "px";
        }
    }
}

/**
 * Event handler for when the user presses the mouse down over a vertical bar
 * that will resize frames based on its position
 *
 * @param event The event of the mouse moving over the vertical bar
 * @param leftitems An array of the items that are on the left of the bar
 * @param rightitems An array of the items that are on the right of the bar
 * @param dragitems The vertical bar
 * @param scrolls An array of items that will be scrolled to the bottom on
 *                  movement
 * @param minX The minimum position which the vertical bar can go to
 * @param maxX The maximum position which the vertical bar can go to
 */
function pag_mouseDownVertical(event, leftitems, rightitems, dragitem,
        scrolls, minX, maxX) {

    // Get the event if it doesn't exist IE
    if (event == null) {
        event = window.event;
    }

    // Get the actual items and their initial positions and widths
    var draggingItemsLeft = new Array(leftitems.length);
    var draggingItemsLeftStartWidth = new Array(leftitems.length);
    var draggingItemsRight = new Array(rightitems.length);
    var draggingItemsRightStartWidth = new Array(rightitems.length);
    var draggingItemsRightStart = new Array(rightitems.length);
    var scrollItems = new Array(scrolls.length);
    var draggingItemVertical = document.getElementById(dragitem);
    var dragItemStart = draggingItemVertical.style.left;
    var cursorStart = pag_mouseX(event);
    for (var i=0; i<leftitems.length; i++)  {
        draggingItemsLeft[i] = document.getElementById(leftitems[i]);
        draggingItemsLeftStartWidth[i] = draggingItemsLeft[i].style.width;
    }
    for (var i=0; i<rightitems.length; i++)  {
        draggingItemsRight[i] = document.getElementById(rightitems[i]);
        draggingItemsRightStartWidth[i] = draggingItemsRight[i].style.width;
        draggingItemsRightStart[i] = draggingItemsRight[i].style.left;
    }
    for (var i=0; i<scrolls.length; i++)  {
        scrollItems[i] = document.getElementById(scrolls[i]);
    }

    // Store the old mouse event handlers
    pag_oldMouseMove = document.onmousemove;
    pag_oldMouseUp = document.onmouseup;

    // Set the event handlers for the dragging
    document.onmousemove = function(event) {
        pag_doDragVertical(event, draggingItemsLeft, draggingItemsRight,
            draggingItemVertical, cursorStart, dragItemStart,
            draggingItemsLeftStartWidth, draggingItemsRightStartWidth,
            draggingItemsRightStart, scrollItems, minX, maxX);
        return false;
    };
    document.onmouseup = function(event) {
        draggingItemVertical.onmouseup();
    };
}

/**
 * Event handler for when the user has the mouse down over a vertical bar
 * that is used to resize frames and then moves the mouse
 *
 * @param event The movement event
 * @param draggingItemsLeft an Array of the items to the left of the bar
 * @param draggingItemsRight an Array of items to the right of the bar
 * @param draggingItemVertical the vertical bar
 * @param cursorStart the start position of the cursor
 * @param dragItemStart the start position of the vertical bar
 * @param draggingItemsLeftStartWidth an Array of the initial widths of the
 *                                        items on the left
 * @param draggingItemsRightStartWidth an Array of the initial widths of the
 *                                        items on the right
 * @param draggingItemsRightStart an Array of the initial positions of the items
 *                                    on the right
 * @param scrollItems an Array of items that will be scrolled down when resized
 * @param minX The minimum position that the bar will go to
 * @param maxX The maximum position that the bar will go to
 */
function pag_doDragVertical(event, draggingItemsLeft, draggingItemsRight,
        draggingItemVertical, cursorStart, dragItemStart,
        draggingItemsLeftStartWidth, draggingItemsRightStartWidth,
        draggingItemsRightStart, scrollItems, minX, maxX) {

    // Get the event if not already got (for IE compatibility)
    if (event == null) {
        event = window.event;
    }

    // Only continue if the mouse position is in range
    var mouseX = pag_mouseX(event);
    if (mouseX >= minX && mouseX <= maxX) {

        // Calculate the amount that the mouse has moved
        var diff = mouseX - cursorStart;

        // Move the vertical bar
        draggingItemVertical.style.left =
            (parseInt(dragItemStart, 10) + diff) + "px";

        // Resize the items on the left
        for (var i=0; i<draggingItemsLeft.length; i++)  {
            draggingItemsLeft[i].style.width =
                (parseInt(draggingItemsLeftStartWidth[i], 10) + diff) + "px";
        }

        // Resize and move the items on the right
        for (var i=0; i<draggingItemsRight.length; i++)  {
            draggingItemsRight[i].style.width =
                (parseInt(draggingItemsRightStartWidth[i], 10) - diff) + "px";
            draggingItemsRight[i].style.left =
                (parseInt(draggingItemsRightStart[i], 10) + diff) + "px";
        }

        // Scroll the scroll items to the bottom
        for (var i=0; i<scrollItems.length; i++)  {
            scrollItems[i].scrollTop = scrollItems[i].scrollHeight;
        }
    }
}

/**
 * Event handler for when the user presses the mouse down over a horizontal bar
 * that will resize frames based on its position
 *
 * @param event The event of the mouse moving over the horizontal bar
 * @param topitems An array of the items that are above the bar
 * @param belowitems An array of the items that are below the bar
 * @param dragitems The horizontal bar
 * @param scrolls An array of items that will be scrolled to the bottom on
 *                  movement
 * @param minY The minimum position which the horizontal bar can go to
 * @param maxY The maximum position which the horizontal bar can go to
 */
function pag_mouseOverHorizontal(event, topitems, bottomitems, dragitem,
        scrolls, minY, maxY) {

    // Get the event if not already got (for IE compatibility)
    if (event == null) {
        event = window.event;
    }

    // Get the actual items and their initial positions an heights
    var draggingItemsTop = new Array(topitems.length);
    var draggingItemsTopStartHeight = new Array(topitems.length);
    var draggingItemsBottom = new Array(bottomitems.length);
    var draggingItemsBottomStartHeight = new Array(bottomitems.length);
    var draggingItemsBottomStart = new Array(bottomitems.length);
    var scrollItems = new Array(scrolls.length);
    var draggingItemHorizontal = document.getElementById(dragitem);
    var dragItemStart = draggingItemHorizontal.style.top;
    var cursorStart = pag_mouseY(event);
    for (var i=0; i<topitems.length; i++) {
        draggingItemsTop[i] = document.getElementById(topitems[i]);
        draggingItemsTopStartHeight[i] = draggingItemsTop[i].style.height;
    }
    for (var i=0; i<bottomitems.length; i++) {
        draggingItemsBottom[i] = document.getElementById(bottomitems[i]);
        draggingItemsBottomStartHeight[i] = draggingItemsBottom[i].style.height;
        draggingItemsBottomStart[i] = draggingItemsBottom[i].style.top;
    }
    for (var i=0; i<scrolls.length; i++) {
        scrollItems[i] = document.getElementById(scrolls[i]);
    }

    // Store the old mouse movement handlers
    pag_oldMouseMove = document.onmousemove;
    pag_oldMouseUp = document.onmouseup;

    // Set the new handlers
    document.onmousemove = function(event) {
        pag_doDragHorizontal(event, draggingItemsTop, draggingItemsBottom,
            draggingItemHorizontal, cursorStart, dragItemStart,
            draggingItemsTopStartHeight, draggingItemsBottomStartHeight,
            draggingItemsBottomStart, scrollItems, minY, maxY);
        return false;
    };
    document.onmouseup = function(event) {
        draggingItemHorizontal.onmouseup();
    };
}

/**
 * Event handler for when the user has the mouse down over a horizontal bar
 * that is used to resize frames and then moves the mouse
 *
 * @param event The movement event
 * @param draggingItemsTop an Array of the items above the bar
 * @param draggingItemsBottom an Array of items below the bar
 * @param draggingItemHorizontal the horizontal bar
 * @param cursorStart the start position of the cursor
 * @param dragItemStart the start position of the horizontal bar
 * @param draggingItemsTopStartHeight an Array of the initial height of the
 *                                        items above
 * @param draggingItemsBottomStartHeight an Array of the initial heights of the
 *                                           items below
 * @param draggingItemsBottomStart an Array of the initial positions of the
 *                                     items below
 * @param scrollItems an Array of items that will be scrolled down when resized
 * @param minY The minimum position that the bar will go to
 * @param maxY The maximum position that the bar will go to
 */
function pag_doDragHorizontal(event, draggingItemsTop, draggingItemsBottom,
        draggingItemHorizontal, cursorStart, dragItemStart,
        draggingItemsTopStartHeight, draggingItemsBottomStartHeight,
        draggingItemsBottomStart, scrollItems, minY, maxY) {

    // Get the event if it has not already been got (for IE compatibility)
    if (event == null) {
        event = window.event;
    }

    // Only continue if the mouse is in range
    var mouseY = pag_mouseY(event);
    if (mouseY >= minY && mouseY <= maxY) {

        // Get the amount by which the mouse has moved
        var diff = mouseY - cursorStart;

        // Move the bar
        draggingItemHorizontal.style.top =
            (parseInt(dragItemStart, 10) + diff) + "px";

        // Resize the items above the bar
        for (var i=0; i<draggingItemsTop.length; i++) {
            draggingItemsTop[i].style.height =
                (parseInt(draggingItemsTopStartHeight[i], 10) + diff) + "px";
        }

        // Resize and move the items below the bar
        for (var i=0; i<draggingItemsBottom.length; i++) {
            draggingItemsBottom[i].style.height =
                (parseInt(draggingItemsBottomStartHeight[i], 10) - diff) + "px";
            draggingItemsBottom[i].style.top =
                (parseInt(draggingItemsBottomStart[i], 10) + diff) + "px";
        }

        // Scroll the items
        for (var i=0; i<scrollItems.length; i++) {
            scrollItems[i].scrollTop = scrollItems[i].scrollHeight;
        }
    }
}

/**
 * An event handler for when the user releases the mouse
 */
function pag_stopDrag() {

    // Restore the event handlers
    document.onmousemove = pag_oldMouseMove;
    document.onmouseup = pag_oldMouseUp;
}

/**
 * Used to calculate the width of a div as a percentage of the width of another
 *
 * @param div The id of the div to calculate the width of
 * @param maindiv The id of the div to calculate as a percentage of
 */
function pag_calculateWidthPercent(div, maindiv) {
    var fullWidth = pag_getWidth(maindiv);
    var width = pag_getWidth(div);
    return (width * 100) / fullWidth;
}
