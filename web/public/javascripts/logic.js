var functionsTree
var variablesTree
var variables = []

function nextNameForVar(baseName) {
	for (var i = 0; true; ++i) {
		var name = baseName+i
		var ok = true
		for (var j in variables) {
			if (variables[j].name == name) {
				ok = false
				break
			}
		}
		if (!ok) continue
		return name 
	}
}

function jsonAjax(url, data, callback, options) {
	var obj = {
		url: url,
		method: 'post',
		data: JSON.stringify(data),
		success: callback ? callback : undefined,
		contentType: 'application/json; charset=utf-8',
		dataType: 'json'
	}
	for (var i in options) obj[i] = options[i]
	$.ajax(obj)
}

function functionForm(func) {
	var form = $('<form />')
	function parameterField(par, getters) {
		var lbl = $('<label />').attr('for', par.name).text(par.name)
		lbl.attr('title', par.desc)
		var input, setter, getter;
		
		switch (par.type) {
		case 'Boolean':
			input = $('<input type=checkbox>').attr('id', par.name)
			
			getter = function() { return input.is(':checked') }
			setter = function(val) { input.prop({checked: val}) }
			break
		case 'BigString':
			input = $('<textarea />').attr('id', par.name)
			input.width(300).height(120)
			
			getter = function() { return input.val() }
			setter = function(val) { input.val(val) }
			break
		case 'Seq':
			input = $('<span />')
			
			var argsList = []
			var count = 0
			
			var addButton = $('<input type=button>').val('+').addClass('add-button')
			addButton.on('click', function() {
				var fieldset = $('<fieldset />')
				var elemGetters = []
				var idx = count
				count += 1
				for (var i in par.seq) {
					fieldset.append(parameterField(par.seq[i], elemGetters))
				}
				var removeButton = $('<input type=button>').val('-').addClass('remove-button')
				removeButton.on('click', function() {
					delete argsList[idx]
					fieldset.hide()
				})
				argsList.push(function() {
					var ret = []
					for (var i in elemGetters) {
						ret.push(elemGetters[i]())
					}
					if (ret.length == 1) ret = ret[0]
					return ret
				})
				fieldset.append(removeButton)
				input.append(fieldset)
			})
			lbl.append(addButton)
			
			getter = function() {
				var ret = []
				for (var i in argsList) {
					ret.push(argsList[i]())
				}
				return ret
			}
			break
		case 'Int':
			input = $('<input>').attr('type','text').attr('id', par.name).attr('title', par.desc)

			getter = function() { return parseInt(input.val()) }
			setter = function(val) { input.val(val) }
			break
		default:
			input = $('<input>').attr('type','text').attr('id', par.name).attr('title', par.desc)

			getter = function() { return input.val() }
			setter = function(val) { input.val(val) }
		}
		getters.push(getter)
		if (par.default != undefined) setter(par.default)
		return $('<div />').addClass('form-group').append(lbl).append(input)
	}
	var getters = []
	for (var i in func.parametersDesc) {
		form.append(parameterField(func.parametersDesc[i], getters))
	}

	var returnLbl = $('<label />').attr('for', 'return').text('Return variable')
	var returnInput = $('<input type=text>').attr('id', 'return').attr('title', func.returnDesc)
	returnInput.attr('placeholder', func.returnType+'_')
	var returnDiv = $('<div />').addClass('form-group').append(returnLbl).append(returnInput)
	form.append(returnDiv)
	
	form.submit(function() {
		var args = []
		for (var i in getters) {
			args.push(getters[i]())
		}
		if (args.length == 1) args = args[0]
		jsonAjax('callFunction', {
			id: func.id,
			args: args
		}, function (data) {
			if (data.type == 'Error') {
				var errorTxt = $('<pre />').html(data.msg)
				$('<div />').append(errorTxt).addClass('ui-state-error')
					.dialog({title: 'Error', width: 'auto',	modal: true})
			} else {
				addVariable(returnInput.val(), data.type, data.ret)
			}
		})
		return false
	})
	return form;
}

function dialogFormForFunction(func) {
	var form = functionForm(func)
	
	var dialog = $('<div />').append(form)
	function execute(d) {
		form.submit()
		$(d).dialog('close')
	}
	dialog.dialog({
		title: func.name,
		width: 'auto',
		modal: true,
		buttons: {
			'Execute': function() { execute(this) },
			'Cancel': function() {
				$(this).dialog('close')
			}
		}
	}).keyup(function(e) {
		if(e.keyCode == 13) execute(this)
    });
}

var gemSymbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
var gemValues = {}
for (var i in gemSymbols) gemValues[gemSymbols[i]] = i
function Gem(str) {
	this.str = str
	var k = 0;
    for (var i = 0; i < str.length; ++i) {
    	if (gemValues[str[i]] != undefined) ++k
    }
    k /= 3
    var capacity = gemSymbols.length
    var length = 1;
    while (k > capacity*length) {
    	capacity *= gemSymbols.length;
    	length++
    }
    k /= length
    
    this.n = 2*k
    this.vertexes = []
    for (var i = 0; i < this.n; ++i) {
    	this.vertexes[i] = []
    	this.vertexes[i].lbl = i+1
    }
    for (var i = 0; i < this.n; ++i) {
    	this.vertexes[i][0] = this.vertexes[i^1]
    }
    
    var idx = 0;
    for (var i=0; i < 3*k; i++) {
    	var c = 0
    	for (var j = 0; j < length; ++j) {
    		var v = gemValues[str[idx++]] 
    		if (v == undefined) --j
    		else c = v*1 + c*gemSymbols.length
    	}
    	var color = Math.floor(i/k)+1
    	this.vertexes[i%k*2][color] = this.vertexes[c*2+1]
    	this.vertexes[c*2+1][color] = this.vertexes[i%k*2]
    }
    
    var positions
	jsonAjax(
		'callFunction',
		{id: 'Gem Vertexes Positions', args: [str, 0]},
		function (data) { positions = data.ret },
		{async: false}
	)
	for (var i in this.vertexes) {
		this.vertexes[i].x = positions[i][0] * 500 + 40
		this.vertexes[i].y = positions[i][1] * 500 + 40
	}
}

function createElement(type, value) {
	if (type == 'Int') {
		return $('<h1 />').text(value)
	}
	if (type == 'String') {
		return $('<h2 />').text(value)
	}
	if (type == 'BigString') {
		return $('<pre />').text(value)
	}
	if (type == 'Matrix') {
		var table = $('<table />').addClass('matrix')
		for (var i in value) {
			var row = $('<tr />')
			for (var j in value[i]) {
				row.append($('<td />').text(value[i][j]))
			}
			table.append(row)
		}
		return table
	}
	if (type == 'Anything') {
		return $('<pre />').text(JSON.stringfy(value))
	}
	if (type == 'Blink') {
		var positions
		jsonAjax(
			'callFunction',
			{id: 'Blink Positions', args: value},
			function (data) { positions = data.ret },
			{async: false}
		)
		var div = document.createElement("div")
		div.classList.add('just-container')
		var svg = d3.select(div).append('svg')
		
		var scale = 400
		var radio = 3
		var stroke = 2
		var g = svg.append('g').attr('transform', 'scale('+scale+') translate(0.12, 0.12)')
		
		g.selectAll('.blink-edge-path').data(positions[1]).enter()
			.append('path')
			.attr('class', 'blink-edge-path')
			.attr('fill', 'none')
			.attr('stroke', function (d) { return d[1] ? 'red' : 'green' })
			.attr('stroke-width', stroke/scale)
			.attr('d', function (d) { return d[0] })
		
		g.selectAll('.blink-vertex-circle').data(positions[0]).enter()
			.append('circle')
			.attr('class', 'blink-vertex-circle')
			.attr('r', radio/scale)
			.attr('stroke', 'black')
			.attr('stroke-width', stroke/scale)
			.attr('fill', 'white')
			.attr('cx', function (d) { return d[0] })
			.attr('cy', function (d) { return d[1] })
		
		return div
	}
	if (type == 'Gem') {
		var gem = new Gem(value)
		var div = document.createElement("div")
		div.classList.add('just-container')
		var svg = d3.select(div).append('svg')
		var gemEdges = []
		for (var c = 0; c < 4; ++c) {
			for (var i in gem.vertexes) {
				var v = gem.vertexes[i]
				if (v.lbl%2 == 1) {
					gemEdges.push({u: v, v: v[c], color: c})
				}
			}
		}
		var gemColors = ['orange', 'blue', 'red', 'green']
		var edge = svg.selectAll('.gem-edge'+i).data(gemEdges).enter().append('path')
		edge.attr('fill', 'none')
			.attr('stroke-width', 3)
			.attr('stroke', function (e){ return gemColors[e.color] })

		var lastScale = 1
		var lastTx = 0
		var lastTy = 0
		var drag = d3.behavior.drag().on('drag', function (v) {
            v.x = X_(d3.event.x)
            v.y = Y_(d3.event.y)
            updateAll()
		});
		function X(x) { return lastScale*x + lastTx }
		function Y(y) {	return lastScale*y + lastTy }
		function X_(x) { return (x-lastTx)/lastScale }
		function Y_(y) { return (y-lastTy)/lastScale }
		var zoom = d3.behavior.zoom().on('zoom', function (d) {
			lastScale = d3.event.scale
			lastTx = d3.event.translate[0]
			lastTy = d3.event.translate[1]
			updateAll()
		})
		svg.append('rect')
			.attr('width', '100%')
			.attr('height', '100%')
			.attr('fill-opacity', 0)
			.call(zoom)
		
		var vertex = svg.selectAll('.gem-vertex').data(gem.vertexes).enter()
			.append('g')
			.attr('class', 'gem-vertex')
			.call(drag)
		vertex.append('circle')
			.attr('class', 'gem-vertex-circle')
			.attr('r', 15)
			.attr('stroke', 'black')
			.attr('stroke-width', '2')
			.attr('fill', function (v){ return v.lbl%2 == 0 ? '#eee' : '#666' })
		vertex.append('svg:text')
			.attr('x', 0)
			.attr('y', 4)
			.attr('class', 'gem-vertex-lbl')
			.text(function (v) { return v.lbl })
			.attr('fill', function (v) { return v.lbl%2 == 0 ? 'black' : 'white' })
			
		function updateAll() {
			vertex.attr('transform', function(v){ return 'translate('+X(v.x)+','+Y(v.y)+')'})
			edge.attr('d', function(e) {
				var cx = (e.u.x + e.v.x)/2
				var cy = (e.u.y + e.v.y)/2
				var dx = -(cy-e.u.y)
				var dy = cx-e.u.x
				var dd = Math.sqrt(dx*dx+dy*dy)
				dx *= (1.5 - e.color)/7
				dy *= (1.5 - e.color)/7
				cx += dx
				cy += dy
				return 'M'+X(e.u.x)+','+Y(e.u.y)+' Q'+X(cx)+','+Y(cy)+' '+X(e.v.x)+','+Y(e.v.y)
			})
		}
		updateAll()
		return div
	}
}

function variableStatus(v) {
	return ''
}

function addVariable(name, type, value) {
	if (!name) {
		name = nextNameForVar(type+'_')
	}
	var node = {name: name, type: type, value: value, 
		callback: function() {
			if (!this.element) {
				this.element = createElement(type, value)
			}
			$('#main-header').text(this.name)
			$('#main-content').html(this.element)
			$('#main-footer').html(variableStatus(this))
		}
	}
	node = variablesTree.addNodes(null, [node])[0]
	variables.push(node)
	node.callback()
}

function loadFunctions() {
	var nodes = functionsTree.getNodes()
	for (var i in nodes) {
		functionsTree.removeNode(nodes[i])
	}
	
	jsonAjax('getFunctions', {} , function (data) {
		function insertOnTree(parent, nodes) {
			for (var i in nodes) {
				var node = nodes[i]
				var newNode = functionsTree.addNodes(parent, [{name: node.name}])[0]
				if (node.subTrees) {
					insertOnTree(newNode, node.subTrees)
				} else {
					newNode.callback = function() {
						dialogFormForFunction(node)
					}
				}
			}
		}
		insertOnTree(null, data)
	})	
}

$(function() {
	doUiLogic()

	loadFunctions();
	
});
