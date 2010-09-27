/*
 * @(#)tree.js	1.5 03/03/01
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved
 *
 * Author: Roger D. Brinkley
 * Modified by Andrew G D Rowley
 */

/*
 * Tree constructor
 *
 * Create a Tree object
 *
 * param name - Name of the tree
 * param container - Name of the element that contains the tree
 * param lineheight - height of a individual line in the tree
 * param selectColor - selection color
 * param showIcon - if true icons are to be displayed, false no icons are
 *     displayed
 * param expandAll - if true expands all the entries with undefined expansion,
 *     false expands only 1 level
 * param linkStyle - the css style to use for the links
 * param imageLocation - the location of the image directory
 * param displayRoot - true if the root node should be displayed
 * param allExpandable - true if all the nodes can be expanded
 */
function Tree(name, container, lineHeight, selectColor, showIcon, expandAll,
        linkStyle, imageLocation, displayRoot, allExpandable) {

    // Data
    this.name = name;
    this.container = container;
    this.lineHeight = lineHeight;
    this.topNodes = new Object();
    this.topNodesCount = 0;
    this.totalNodes = 0;
    this.selectedNode = null;
    this.selectedBG = null;
    this.selectColor = selectColor;
    this.showIcon = showIcon;
    this.expandAll = expandAll;
    this.linkStyle = linkStyle;
    this.imageLocation = imageLocation;
    this.displayRoot = displayRoot;
    this.allExpandable = allExpandable;

    // methods
    this.addTreeNode = addTreeNode;
    this.addTreeNodeRef = addTreeNodeRef;
    this.findTreeNodeForIDNum = findTreeNodeForIDNum;
    this.getTreeNodeExpand = getTreeNodeExpand;
    this.drawTree = drawTree;
    this.drawTree = drawTree;
    this.drawTreeNode = drawTreeNode;
 	this.deleteTreeNode = deleteTreeNode;
  	this.deleteTreeNodeRef = deleteTreeNodeRef;
    this.refreshTree = refreshTree;
    this.refreshTreeNode = refreshTreeNode;
    this.toggle = toggleTreeNode;
    this.select = selectTreeNode;
    this.doubleClick = doubleClickTreeNode;
    this.mouseUp = mouseUpTreeNode;
}

/**
 * Add a TreeNode to the tree
 *
 * param parent - Name of the parent
 * param idnum - Name for this object
 * param icon - Image to be displayed for this TreeItem - null means default
 * param content - Display content for the node.
 * param expandType - -1 if program depended; 1 if children should be shown;
 *                    0 otherwise
 *
 */
function addTreeNode(parent, idnum, icon, content, expandType, selectCode,
        doubleClickCode, expandCode, collapseCode, isSelected, extraCell) {


    // ignore nulls
    if (idnum == "null") {
        return;
    }

    var node = new TreeNode(idnum, icon, content, expandType, selectCode,
        doubleClickCode, expandCode, collapseCode, isSelected, extraCell);
    this.totalNodes++;
    if (parent == null || parent == "null" || parent == "root") {
        this.topNodes[this.topNodesCount] = node;
        this.topNodesCount++;
        node.setParent(null);
    } else {
        parentNode = this.findTreeNodeForIDNum(parent);
        if (parentNode == null) {
     //       alert ("parent " + parent + " for idnum " + idnum
     //           + " doesn't exist");
            node.setParent(null);
        } else {
            parentNode.addChild(this, node, expandType);
            node.setParent(parentNode);
        }
    }
}

function addTreeNodeRef(parent, idnum, icon, content, expandType, selectCode,
        doubleClickCode, expandCode, collapseCode, isSelected, extraCell) {

	var exists=this.findTreeNodeForIDNum(idnum);
	if (exists){
			var text=exists.content;
			var nstart=text.lastIndexOf("[");
			var nend=text.lastIndexOf("]");
			var tnum=" [2]";
			if ((nend!=-1)&&(nstart!=-1)) {
				nend--;
				var numt=text.substr(nstart+1,nend-nstart);
				var num=numt*1;
				if (num != NaN){
					num++;
					var tnum=" ["+num+"]";
				} else {
					nstart=text.length;
				}
			}
			exists.content=content+tnum;
			return false;
	}
    // ignore nulls
    if (idnum == "null") {
        return false;
    }

    var node = new TreeNode(idnum, icon, content, expandType, selectCode,
        doubleClickCode, expandCode, collapseCode, isSelected, extraCell);
    this.totalNodes++;
    if (parent == null || parent == "null" || parent == "root") {
        this.topNodes[this.topNodesCount] = node;
        this.topNodesCount++;
        node.setParent(null);
    } else {
        parentNode = this.findTreeNodeForIDNum(parent);
        if (parentNode == null) {
    //        alert ("parent " + parent + " for idnum " + idnum
    //            + " doesn't exist");
            node.setParent(null);
        } else {
            parentNode.addChild(this, node, expandType);
            node.setParent(parentNode);
        }
    }
    return true;
}

/**
 * Find the Node for a given idnum
 */
function findTreeNodeForIDNum(idnum) {
    for (var i = 0; i < this.topNodesCount; i++) {
        var node = this.topNodes[i].findIDNum(idnum);
        if (node != null) {
            return node;
        }
    }
    return null;
}

function getTreeNodeExpand(idnum){
    var node = this.findTreeNodeForIDNum(idnum);
    if (node != null) {
		return node.expand;
	}
	return 0;
}


function deleteNode(parent, idnum) {
    var ntn=new Array();
	var ntnc=0;
	var changed=false;
    for (var i = 0; i < parent.numChildren; i++) {
        if (parent.children[i].idnum==idnum)
		{
			changed=true;
		}else{
			ntn[ntnc]=parent.children[i];
			ntnc++;
		}
	}
	if (changed){
		parent.children=ntn;
		parent.numChildren=ntnc;
	} else {
	    for (var i = 0; i < parent.numChildren; i++) {
			if (deleteNode(parent.children[i], idnum))
				return true;
		}
	}
	return changed;
}

function deleteNodeRef(parent, idnum) {
    var ntn=new Array();
	var ntnc=0;
	var changed=false;
 	var tnum="";
    for (var i = 0; i < parent.numChildren; i++) {
        if (parent.children[i].idnum==idnum)
		{
			var text=parent.children[i].content;
			var nstart=text.lastIndexOf("[");
			var nend=text.lastIndexOf("]");

			if ((nend!=-1)&&(nstart!=-1)) {
				nend--;
				var num=text.substr(nstart+1,nend-nstart)*1;
				if (num != NaN) {
					num--;
					if (num>1) {
						tnum="["+num+"]";
					}
					parent.children[i].content=text.substr(0,nstart)+tnum;
				} else
					changed=true;
			} else
				changed=true;
		}else{
			ntn[ntnc]=parent.children[i];
			ntnc++;
		}
	}
	if (changed){
		parent.children=ntn;
		parent.numChildren=ntnc;
	} else {
	    for (var i = 0; i < parent.numChildren; i++) {
			if (deleteNodeRef(parent.children[i], idnum))
				return true;
		}
	}
	return changed;
}

/**
 * Delete the Node with a given idnum
 */

function deleteTreeNode(idnum) {
    var ntn=new Array();
	var ntnc=0;
	var changed=false;
 	this.selectedNode = null;
    for (var i = 0; i < this.topNodesCount; i++) {
        if (this.topNodes[i].idnum==idnum)
		{
			changed=true;
		}else{
			ntn[ntnc]=this.topNodes[i];
			ntnc++;
		}
	}
	if (changed){
		this.topNodes=ntn;
		this.topNodesCount=ntnc;
	} else {
    	for (var i = 0; i < this.topNodesCount; i++) {
        	if (deleteNode(this.topNodes[i],idnum))
        		break;
       	}
    }
}

function deleteTreeNodeRef(idnum){
	    var ntn=new Array();
	var ntnc=0;
	var changed=false;
 	this.selectedNode = null;
    for (var i = 0; i < this.topNodesCount; i++) {
        if (this.topNodes[i].idnum==idnum)
		{
			changed=true;
		}else{
			ntn[ntnc]=this.topNodes[i];
			ntnc++;
		}
	}
	if (changed){
		this.topNodes=ntn;
		this.topNodesCount=ntnc;
	} else {
    	for (var i = 0; i < this.topNodesCount; i++) {
        	if (deleteNodeRef(this.topNodes[i],idnum))
        		break;
       	}
    }
}


/**
 * Draw the Tree
 */
function drawTree() {

    var html = "";

    for (var i = 0; i < this.topNodesCount; i++) {
        html += this.drawTreeNode(this.topNodes[i], new Object(), 0, 0,
            (i == 0), (i == (this.topNodesCount - 1)), "");
    }
    document.getElementById(this.container).innerHTML = html;
    if (this.selectedNode != null) {
	    var style = document.getElementById(
	        this.selectedNode.idnum + "Content").style;
	    this.selectedBG = style.backgroundColor;
	    style.backgroundColor = this.selectColor;
    }
}

/**
 * Draw the node and it's children.
 * If the node is null draw all the topNodes
 *
 * param node - the node (and children) to draw
 *
 */
function drawTreeNode(node, isLine, isLineCount, level, isFirst, isLast, html) {
    node.isLast = isLast;
    node.isFirst = isFirst;
    node.level = level;

    if (node.isSelected) {
        this.selectedNode = node;
    }

    // create a <DIV>
    if (level > 0 || this.displayRoot) {
        html += "<div id=\"" + node.idnum + "\""
            + " style=\"position: relative; visibility: visible\">";

        // create a table for this node with only one table row
        html += "<table cellspacing=\"0\""
            + " cellpadding=\"0\" border=\"0\">";
        html += "<tr>";

        // create table descriptions <td> for the heirarchy lines (or spaces)
        // there are no heirarchical lines for the first node or children of
        // the first node
        for (var i = 0; i < isLineCount - 1; i++) {
            if (isLine[i] == true) {
                html += "<td style=\"white-space: nowrap;\">"
                    + "<img src=\"" + this.imageLocation
                    + "tree_linevertical.gif\""
                    + " border=\"0\" width=\"16\" height=\"22\""
                    + " style=\"display: block;\"></td>";
            } else {
                html += "<td style=\"white-space: nowrap;\">"
                    + "<img src=\"" + this.imageLocation + "tree_blank.gif\""
                    + " border=\"0\" width=\"16\" height=\"22\""
                    + " style=\"display: block;\"></td>";
            }
        }

        // create the heirarchy line if a leaf or the turner if a node
        // Don't bother is if this is a top node
        if (isLineCount > 0) {
            if ((node.numChildren == 0) && !this.allExpandable) {
                var image = null;
                if (isFirst && (level == 1) && !this.displayRoot) {
                    image = this.imageLocation + "tree_linefirstnode.gif";
                } else if (isLine[isLineCount - 1]) {
                    image = this.imageLocation + "tree_linemiddlenode.gif";
                } else {
                    image = this.imageLocation + "tree_linelastnode.gif";
                }
                html += "<td style=\"white-space: nowrap;\">"
                        + "<img src=\"" + image + "\" border=\"0\""
                        + " width=\"16\" height=\"22\""
                        + " style=\"display: block;\"></td>";
            } else {
                var image = null;

                // an expandable node
                if (node.expand) {

                    if (isFirst && (level == 1) && !this.displayRoot) {
                        image = this.imageLocation + "tree_handledownfirst.gif";
                    } else if (isLine[isLineCount - 1]) {
                        image = this.imageLocation
                            + "tree_handledownmiddle.gif";
                    } else {
                        image = this.imageLocation + "tree_handledownlast.gif";
                    }
                } else {
                    if (isFirst && (level == 1) && !this.displayRoot) {
                        image = this.imageLocation
                            + "tree_handlerightfirst.gif";
                    } else if (isLine[isLineCount-1]) {
                        image = this.imageLocation
                            + "tree_handlerightmiddle.gif";
                    } else {
                        image = this.imageLocation + "tree_handlerightlast.gif";
                    }
                }
            html += "<td style=\"white-space: nowrap;\">"
                + "<img style=\"cursor: pointer; display: block;\" id=\""
                + node.idnum + "Toggle\" onClick=\"return "
                + this.name + ".toggle('" + node.idnum
                + "')\" src=\"" + image + "\""
                + " border=\"0\" width=\"16\" height=\"22\"></td>";
            }
        }

        // create a table definition for the image
        // use the node icon if supplied otherwised use the default folder or
        // document
        if (this.showIcon && node.icon!="null" && node.icon != null) {
            var image = null;
            if (node.icon != null && node.icon != "null") {
                image = node.icon;
            } else {
                if (node.numChildren == 0) {
                    image = this.imageLocation + "tree_document.gif";
                } else {
                    image = this.imageLocation + "tree_folder.gif";
                }
            }
            html += "<td style=\"white-space: nowrap;\">"
                    + "<img style=\"cursor: pointer; display: block;\""
                    + " onClick=\"return " + this.name + ".select('"
                    + node.idnum + "')\" src=\"" + image + "\""
                    + " onDblClick=\"return " + this.name + ".doubleClick('"
                    + node.idnum + "')\""
                    + " border=\"0\"></td><td width=\"2px\"> </td>";
        }

        // create the content
        html += "<td style=\"white-space: nowrap;\">"
            + "<span id=\"" + node.idnum
            + "Content\" style=\"" + this.linkStyle + "\""
            + " onClick=\"return "
            + this.name + ".select('" + node.idnum + "')\""
            + " onDblClick=\"return "
            + this.name + ".doubleClick('" + node.idnum + "')\""
            + " onMouseDown=\"return false;\""
            + " onMouseUp=\"return "
            + this.name + ".mouseUp(event, '" + node.idnum + "');\""
            + " onSelectStart=\"return false;\"" + ">"
            + node.content
            + "</td>";
		if (node.extraCell!=null){
			html +="<td style=\"white-space: nowrap;\">&nbsp</td><td style=\"white-space: nowrap;\">"+node.extraCell+"</td>";
		}


        // finish off the necessary details for this table row and table
        html += "</tr>";
        html += "</table>";
        html += "</div>";
    }

    // All done now draw any children
    for (var i = 0; i < node.numChildren; i++) {

        // set the IsLine Array appropriately
        if (i == node.numChildren - 1) {
            isLine[isLineCount] = false;
        } else {
            isLine[isLineCount] = true;
        }
        html = this.drawTreeNode(node.children[i], isLine, isLineCount + 1,
            level + 1, i == 0, i == (node.numChildren - 1), html);
    }
    return html;
}

/**
 * Refresh the Tree based on the hierarchical visibility
 */
function refreshTree() {

    // call refreshTreeNode for all the topNodes
    // this will recursively call
    var lineNumber = 0;
    for (var i=0; i < this.topNodesCount; i++) {
        lineNumber = this.refreshTreeNode(this.topNodes[i], true, lineNumber);
    }
}

/**
 * Refresh a Node based on the nodes and ancestoral visibility
 *
 * param node - node to refresh
 * param ancestorOpen - true if ancestor is visible, false otherwise
 */
function refreshTreeNode(node, ancestorOpen, lineNumber) {
    if (ancestorOpen) {
        lineNumber++;
        node.visible = true;
        node.lineNumber = lineNumber;
        if (node.parent != null || this.displayRoot) {
            document.getElementById(node.idnum).style.display = 'inline';
        }
    } else {
        node.visible = false;
        node.lineNumber = 0;
        if (node.parent != null || this.displayRoot) {
            document.getElementById(node.idnum).style.display = 'none';
        }
    }

    // All done now refresh the children
    for (var i = 0; i < node.numChildren; i++) {
        var nodeOpen = ancestorOpen;

        // if the ancestor is open but the node is not then
        // set the visibility to false for the childe
        if (ancestorOpen && !node.expand) {
            nodeOpen = false;
        }

        lineNumber = this.refreshTreeNode(node.children[i], nodeOpen,
            lineNumber);
    }
    return lineNumber;
}

/**
 * Toggle the tree node
 *
 * param idnum - the idnum of the Node to toggle
 */
function toggleTreeNode(idnum) {

    // get the node
    var node = this.findTreeNodeForIDNum(idnum);
    if (node == null) {
        alert("internal error - " + idnum + " not found in toggleTreeNode");
        return;
    }

    // change the internal data structure
    if (node.expand) {
        node.setExpand(false);
    } else {
        node.setExpand(true);
    }

    // select the appropriate image depending on which child I am
    // and if I'm expanded or not
    if (node.expand) {
        if (node.isFirst && (node.level == 1) && !this.displayRoot) {
            imgsrc = this.imageLocation + "tree_handledownfirst.gif";
        } else if (!node.isLast) {
            imgsrc = this.imageLocation + "tree_handledownmiddle.gif";
        } else {
            imgsrc = this.imageLocation + "tree_handledownlast.gif";
        }
    } else {
        if (node.isFirst && (node.level == 1) && !this.displayRoot) {
            imgsrc = this.imageLocation + "tree_handlerightfirst.gif";
        } else if (!node.isLast) {
            imgsrc = this.imageLocation + "tree_handlerightmiddle.gif";
        } else {
            imgsrc = this.imageLocation + "tree_handlerightlast.gif";
        }
    }

    document.getElementById(node.idnum + "Toggle").src = imgsrc;

    this.refreshTree();

    // this done to cancel click action
    return false;
}

/**
 * select the tree node
 *
 * param idnum - the idnum of the Node to toggle
 */
function selectTreeNode(idnum) {

    // get the node
    var node = this.findTreeNodeForIDNum(idnum);
    if (node == null) {
        alert ("internal error - " + idnum + " not found in toggleTreeNode");
        return false;
    }

    // leave selected items
    if (this.selectedNode == node) {
        return false;
    }

    // change the background on the selected node
    if (this.selectedNode != null){
        var style = document.getElementById(this.selectedNode.idnum
            + "Content").style;
        style.backgroundColor = this.selectedBG;
        this.selectedNode.isSelected = false;
    }

    // make the new node the slected node
    this.selectedNode = node;
    var style = document.getElementById(idnum + "Content").style;
    this.selectedBG = style.backgroundColor;
    style.backgroundColor = this.selectColor;

    // display the selection in the contents
    node.select();

    // this done to cancel click action
    return false;
}

/**
 * double click the tree node
 *
 * idnum - the idnum of the node to double click
 */
function doubleClickTreeNode(idnum) {

    // get the node
    var node = this.findTreeNodeForIDNum(idnum);
    if (node == null) {
        alert("internal error - " + idnum
            + " not found in doubleClickTreeNode");
        return false;
    }

    // run the action
    node.doubleClick();

    // Return false to cancel the double click action
    return false;
}

/**
 * The user lifts the mouse button up over a node
 *
 * event - The event of the mouse button
 * idnum - The idnum of the node clicked
 */
function mouseUpTreeNode(event, idnum) {
    if (event == null) {
        event = window.event;
    }
    if (event.which == 3 || event.which == 2
            || event.button == 3 || event.button == 2) {
        return false;
    }
}

/**
 * TreeNode constuctor
 *
 * Create a TreeNode for use in a Tree
 *
 * param idnum - Id number for the tree Node
 * param icon - image to display for this TreeNode - null means use default
 * param content - content to display in tree - generally the name
 * param expandType - should children of the TreeItem be expanded
 * param selectCode - the code to be executed when selected
 * param doubleClickCode - the code to be executed when double clicked
 * param expandCode - the code to be executed when the node is expanded
 * param collapseCode - the code to be executed when the node is collapsed
 */
function TreeNode(idnum, icon, content, expandType, selectCode, doubleClickCode,
        expandCode, collapseCode, isSelected, extraCell) {
    this.parent = null;
    this.idnum = idnum;
    this.icon = icon;
    this.content = content;
    this.expand = true;
    this.extraCell=extraCell;
    if (expandType == "0" || expandType == "false" || expandType == false
            || expandType == 0) {
        this.expand = false;
    }
    this.selectCode = selectCode;
    this.doubleClickCode = doubleClickCode;
    this.expandCode = expandCode;
    this.collapseCode = collapseCode;
    this.visible = false;
    this.isSelected = isSelected;
    this.children = new Array();
    this.numChildren = 0;
    this.level = 0;
    this.lineNumber = 0;
    this.isLast = false;
    this.isFirst = false;

    // methods
    this.addChild = addChild;
    this.setParent = setParent;
    this.findIDNum = findIDNum;
    this.setExpand = setExpand;
    this.setAncestorExpand = setAncestorExpand;
    this.select = selectNode;
    this.doubleClick = doubleClickNode;
}

/**
 * Add a child object to the TreeItem
 *
 * param child - Child TreeItem object to add
 */
function addChild(tree, child, expandType) {
    this.children[this.numChildren] = child;
    this.numChildren++;
    child.level = this.level + 1;
    if (tree.expandAll) {
        if (expandType == "-1") {
            this.expand = true;
        }
    } else {
        if (expandType == "-1" && child.level > 1) {
            this.expand = false;
        }
    }
}


/**
 * Recursively find an idnum that matches the parameter
 *
 * param idnum - ID number to find
 *
 * returns the matching Node or null if no matches
 */
function findIDNum(idnum) {

    // if this node matches return it
    if (this.idnum == idnum) {
        return this;
    }

    // humm no matches see if the children match
    for (var i = 0; i < this.numChildren; i++) {
        var node = this.children[i].findIDNum(idnum);
        if (node != null) {
            return node;
        }
    }

    // no matches return null
    return null;
}

/**
 * Set the parent object for the TreeItem
 *
 * param parent - Parent object for this treeItem - null is valid
 */
function setParent(parent) {
    this.parent = parent;
}

/**
 * Set the expand flag for the TreeItem
 *
 * param type - type of expansion: -1 program default; 0 close; 1 open
 */
function setExpand(type) {
    this.expand = type;
    if (this.expand && (this.expandCode != null)) {
        eval(this.expandCode);
    } else if (!this.expand && (this.collapseCode != null)) {
        eval(this.collapseCode);
    }
}

/**
 * recursively sets the ancestors to be expanded
 */
function setAncestorExpand() {
    if (this.parent != null) {
        this.parent.setExpand(true);
        this.parent.setAncestorExpand();
    }
}

function selectNode() {
    this.isSelected = true;
    if (this.selectCode != null) {
        eval(this.selectCode);
    }
}


function doubleClickNode() {
    if (this.doubleClickCode != null) {
        eval(this.doubleClickCode);
    }
}


