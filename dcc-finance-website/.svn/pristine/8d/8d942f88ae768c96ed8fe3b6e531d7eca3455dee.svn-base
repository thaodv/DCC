	/*通过class获取元素*/
	function getByClass(oParent,sClass){
		if(oParent){
			if(oParent.getElementsByClassName){				
				return oParent.getElementsByClassName(sClass);
			}else{
				var arr=[];
				var reg = new RegExp("\\b"+sClass+"\\b");
				var aEle = document.getElementsByTagName('*');
				for(var i=0;i<aEle.length;i++){
					if(reg.test(aEle[i].className)){						
						arr.push(aEle[i]);
					}					
				}
				return arr;				
			}
			
		}else{
			
			alert("获取的元素无效");
		}
		
	}
	
	/*
	* 移动端 滑动事件
	* type有 start / move / left / right / up / down / long / click /end
	*  
	*/
	function addTouchEvent(obj,type,fn){
	     //滑动范围在5x5内则做点击处理，s是开始，e是结束
		 var init = {x:5,y:5,sx:0,sy:0,ex:0,ey:0};
		 var sTime = 0, eTime = 0;
		 type = type.toLowerCase();
		 
		obj.addEventListener("touchstart",function(ev){
			var oEvent=ev||event;
			sTime = new Date().getTime();
			init.sx = event.targetTouches[0].pageX;
			init.sy = event.targetTouches[0].pageY;
			init.ex = init.sx;
			init.ey = init.sy;
			if(type.indexOf("start") != -1){
				if(fn.apply(obj,arguments)==false){
					
					oEvent.cancelBubble=true;
					oEvent.preventDefault();
				}
			}
		}, false);
		
		obj.addEventListener("touchmove",function(ev) {
			var oEvent = ev ||event;
			oEvent.preventDefault();//阻止触摸时浏览器的缩放、滚动条滚动
			init.ex = event.targetTouches[0].pageX;
			init.ey = event.targetTouches[0].pageY;
			if(type.indexOf("move")!=-1){
				if(fn.apply(obj,arguments)==false){
					
					oEvent.cancelBubble=true;
					oEvent.preventDefault();
				}
			};
		}, false);

		obj.addEventListener("touchend",function(ev) {
			var oEvent = ev || event;
			var changeX = init.sx - init.ex;
			var changeY = init.sy - init.ey;
			if(Math.abs(changeX)>Math.abs(changeY)&&Math.abs(changeX)>init.x) {
				//左右事件
				if(changeX > 0) {
					if(type.indexOf("left")!=-1){
						if(fn.apply(obj,arguments)==false){
							
							oEvent.cancelBubble=true;
							oEvent.preventDefault();
						}
					};
				}else{
					if(type.indexOf("right")!=-1){
						if(fn.apply(obj,arguments)==false){
							
							oEvent.cancelBubble=true;
							oEvent.preventDefault();
						}
					};
				}
			}
			else if(Math.abs(changeY)>Math.abs(changeX)&&Math.abs(changeX)>init.x){
				//上下事件
				if(changeY > 0) {
					if(type.indexOf("up")!=-1){
						if(fn.apply(obj,arguments)==false){
							
							oEvent.cancelBubble=true;
							oEvent.preventDefault();
						}
					};
				}else{
					if(type.indexOf("down")!=-1){
						if(fn.apply(obj,arguments)==false){
							
							oEvent.cancelBubble=true;
							oEvent.preventDefault();
						}
					};
				}
			}
			else if(Math.abs(changeX)<init.x && Math.abs(changeY)<init.y){
				eTime = new Date().getTime();
				//点击事件，此处根据时间差细分下
				if((eTime - sTime) > 300) {
					if(type.indexOf("long")!=-1){
						if(fn.apply(obj,arguments)==false){
							
							oEvent.cancelBubble=true;
							oEvent.preventDefault();
						}
					}; //长按
				}
				else {
					if(type.indexOf("click")!=-1){
						if(fn.apply(obj,arguments)==false){
							
							oEvent.cancelBubble=true;
							oEvent.preventDefault();
						}
					}; //当点击处理
				}
			}
			if(type.indexOf("end")!=-1){
				if(fn.apply(obj,arguments)==false){
							
					oEvent.cancelBubble=true;
					oEvent.preventDefault();
				}
			};
		}, false);		
		
	 }