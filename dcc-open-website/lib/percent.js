/**
 *
千分位小数 1,234.0000
 *@method 方法名
 *@param {Type} num 数值
         {Type} point 保留几位小数,默认为2
 *@return {返回值类型} 小数点前有千分位，小数点后没有千分位
 */
function per(num,point){
  point=point || point == 0?point:2;
  if(num){
    return Number(num).toFixed(point).replace(/(\d)(?=(\d{3})+\.)/g, '$1,')
  }
  return 0;
};
/**
 *
 *@method 地址 1234...sdsd
 *@param {Type} address 数值
         {Type} len 保留前后各几位有效数字
 *@return {返回值类型} 前后数量相同,中间省略号
 */
function trimAddress(address,len){
  if(address && address.length > 17){
      return address.substring(0, len) + "..." + address.substr(address.length-len);
  }
  return address;
}