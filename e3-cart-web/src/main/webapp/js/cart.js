/**
 * .increment 类选择器，在这个类上绑定一个事件。(这个类在页面显示为+号)点击这个加号，就会调用这个方法，会将页面的商品数量加一，
 * 并且会发送ajax请求到后台。将商品数量的变化更新到cookie中。并重新计算商品总价。
 * 
 * 1、将本节点转换为jquery节点，并取本节点一个属性为input的兄弟节点。 2、给input节点重新赋值。
 * 3、发送ajax请求到后台，更改cookie中商品数量 4、重现计算总价。
 */

var CART = {
	itemNumChange : function(){
		
		$(".increment").click(function(){// ＋
			var _thisInput = $(this).siblings("input");
			_thisInput.val(eval(_thisInput.val()) + 1);
			$.post("/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val() + ".action",function(data){
				CART.refreshTotalPrice();
			});
		});
		$(".decrement").click(function(){// -
			var _thisInput = $(this).siblings("input");
			if(eval(_thisInput.val()) == 1){
				return ;
			}
			_thisInput.val(eval(_thisInput.val()) - 1);
			$.post("/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val() + ".action",function(data){
				CART.refreshTotalPrice();
			});
		});
		/*
		 * $(".itemnum").change(function(){ var _thisInput = $(this);
		 * $.post("/service/cart/update/num/"+_thisInput.attr("itemId")+"/"+_thisInput.val(),function(data){
		 * CART.refreshTotalPrice(); }); });
		 */
	},
	refreshTotalPrice : function(){ // 重新计算总价
		var total = 0;
		$(".itemnum").each(function(i,e){
			var _this = $(e);
			total += (eval(_this.attr("itemPrice")) * 10000 * eval(_this.val())) / 10000;
		});
		$("#allMoney2").html(new Number(total/100).toFixed(2)).priceFormat({ // 价格格式化插件
			 prefix: '¥',
			 thousandsSeparator: ',',
			 centsLimit: 2
		});
	}
};

$(function(){
	CART.itemNumChange();
});