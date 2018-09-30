package worhavah.certs.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64toBitTools {

    public static String tt="/9j/4AAQSkZJRgABAQIAJgAmAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCADTAaEDASIAAhEBAxEB/8QAHQABAAEFAQEBAAAAAAAAAAAAAAECAwQFBwgGCf/EAD0QAAIBAwIEAwcCBQIFBQEAAAABAgMEEQUhBhITUQcxQQgiMlNhcZEUQiMzUmKBFRYXJDShsRhDcoKywf/EABsBAQACAwEBAAAAAAAAAAAAAAABBAIDBQYH/8QAJREBAAICAQQCAgMBAAAAAAAAAAECAxEEBRIhMRNBFCIGMlFx/9oADAMBAAIRAxEAPwD0qAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkYfYCATh9hv2AgE4fZjfsBAJ37DD7AQCcPsxv2AgE79hv2AgE79hv2AgE79iAAJw+zG/YCATv2G/YCATv2GH2AgE4fZjfsBAJw+ww+wEAnfsN+wEAnfsN+wEAnfsN+wEAnfsN+wEAnfsN+wEABNPy3+wAE79hv2AgAAAAAAAAAAAAAAAAYzsgAK0pThONJe/jzJ0ylcRpyV2k5Z2Zcs/iMv1Ao6cUOSPlguACyoLlfcs3Fejbwi6nqZb88lmvb06695ATS6dampw8ii5pydvNUV/Exsy7SpxpRUY+RcQGBp1GvGni6acjL6UUy4AKOnHsOnHsVgCjpx7Dkj2KwBbcILf0MSvZ9arCcajiovLXczcJ57kS2iuZZ+wEckSenHsSioCjpx7Dpx7FYAo6cexbrU80pqn8eNvuXwBr9No3MYf8202ZnSinkuADC1Gqra354wbf0RXaSVa2jNxab7mU0n5oYwBb6cRyRLgAo6cew6cexWAKOnHsOnHsVgC3yR7Dkh2LjKWBTyR7Dkj2MKN7N3/R5HydzPy+ZL0ApdOO+2zLdK3p0sqC3ZkAC304k9OPYrAFmVGD8olH6aPk3hmSAMb9PFbLdkdDEZZXkjKZEvgl9gNdQXPadR/FnBT6lyz/AOgl92W4/CAAAAAAAAAAAGRZ/EZfqYln8RlS+gFWSMrGzRYnzbSj5rz+py3xI8U6nB2vLTKOmxuHKn1OaUsEWntbKYrZJ1V1nK8iMRR5+/4830aijPQ4RljmfvehbXjzeVLmMo6TFU3s8yNXyrsdL5ExvtehfdW5LaxnOx58fj3d88ow0aMmnjLkXP8AjzcxXv6NTlF7bTMq5ImNyiel8mI32vQGV3Byrwx8Tq3Gms17GrpitHShzKSecnUKbb8/MyraLelPJitjnVva6ADJrARIpAt3KlKhNUX7/oY2lRuowkrrfsZqYbYg1/qUVFGWS/IQT4VApTBja0xOhUCh4eX2ITbWWZ6RErgKUT6EJSCzhqXvS/8AqfO8W8WaRwdYfr+Ib1UKEpKMUt5b/QmsTadQiX1AyeZNc9p5WWt3VvpujRvLGM+WhXc2nNfYsVvaY1S3zVu+E6tK3fwualFP/LLMcLNP0jueosoHFPCPxvsuOdRradqlGnpl+97eDl7s1936nYKsujyTqNxituVb5NGTFfFOrxo2y8kOUUsuSS7tluMcc8l5SMe8tP1Fk6OWsvJr2yZikpLMWmvoQyzZ26taCgm3juX1utxsUqKzzOK5iv8A8gEgAylkbFQKRkbFWQQ0PInYllM/gl9iplE/hl9gMKz/AOgl92W4/CXLRcthKPqmy3+1AAAAAAAAAAABkWfxGVPKTwa+nU5alNP1Znyb5JNLL7BErFTqS5XGXKk8yXdHEvaOsNNpWdrqEqUal/OfLjmw0jt8lLlSzut2jgXtB8PVevb6/CrXuoVX0P0sItqP1wYZI3C5wZil4mZcw4b4b1fiOrK00yi61aPvyb2UYdsnVLTwG61vRqXOqyp1HFN00tos2Ps+1L+npVelW06VO05nyXM44m3/AE/Y7HGM5TjvmPbsaq0XuZ1DNE9tJ8OLT8BqEm5f6xPCWPhOV8e8LUeEdaWl0bpXMJw55TznlPWl9e0tLsrm9vW4W9FOU8b7dzxvxLcrVde1C5pSnVp16znRaeW45MM1NeIb+l8nPkmZyT4dY9nbQ79a1c6tOnKOndLpQlJYcpdzv0Jc0uy7fU0HAtPl4S0tKPTcaEeaPLjc+iik0mvM3YcfZDj8vJ8mWZlcBH7ilLlX3ZtV1UiESUyfuvCywD8tynO5b5lH3aj96Xp2Nff6xYadQrV7m6oxVCLclzrOPsa75O2dMq45t6htk1nHqG98HNX4w8JY5leS5pPHl5F638WuFq1zTt6d7LnqPli3H1JtkisbWa8Dkx5mkuipohtYfc1tnfULmDq0a0Z0l8Ti87mROTk4yi/cl+8yraL+VPJF8c6mF5JqcWvJmJRvnPVJWvJJRSznGxmxjgrSWc4We5MztPsRLeESy1LPp5kWGu1LWLHT6VWteXEKFKlBznOTxhI8T+PniBQ8QOJo1LCEoaZYp06c8/zt/iwekvaJ0zS6/h1dvUb+pp/vcyqQ355ekX9zxJaTpUZUqtWHWpU5pzpeSnHPl/k7vSuNW+8k/TXeXoX2efByGq0rXifiSEZWPx2tt5qp2kz0D4gcN6VrHCWo0NQsqFW2p0JThTUFFqUVs8lnwqvrbUvDzRrnTLNWlpUpLkt8/wAv7H01xSje0q9vVi5UpLp1Iv8AdF+Zz83Ivky99pZRD826VaVrqHVtJSpV6NVyg4y+HD23PXvsv8Y63xbpesS4gvne1aFSMaXMvhjg03EXsyaffatd3em6xKxtasuanQUc8n0ydY8L+AdN8P8ARFYWD6lxU3r1fWo+5a5vMw5scRHsiNPtoeXL2Cy5ZKYblxI5DJTvzYZX5kLzJAABiBBD8yUUYxJyE2iPYqC+hptW1m20uVP9XUcep2WS1pnEFleVujb1HJ/VFSeZj7u2JbIwW1tv8gtvzRcRbtG4aonzpPoUsq9Cn1JSwY4jUuIZ2SLS/lw+5NePSu6mf/e8ipvbPYCkAAAAAAAAeQDAoqP+PR+sjay2jt6mqqRTnGrj+VubOE1OEJ4+LcCzUlKKe3KvSRROEZwj1KMKi9eaOcfUy8YTJ9BPlj2+dwxlTjTj/DhCMX+2KwiZZUVGK5W/J/Uu1WuXyKOZY32+pGtQy8z4cd8eeLJWVhT0nTq7V7P+fTa+OBzPwb4bWvcYU1T5relaPrtuOYz/ALTF8VZatU4xvFrModZSfQlB5zT9MncfAm206hwhGWm143TlLNWpjeEv6clekTe25d28xx+JHb7l0eMMSXK1GCWOVIorTlSpTqOO0d1gvR8iX9fIsy4MxFpYul3LureU2nF59TLT8kEkvJJfYn0CUNlDf4JqLMSnOMJhEvgPFziqpwxoLnb8ru6z5ILO8P7sHnPQ9P1Pi/iJ0ld1q11OWar5mly98HQ/aK026etUNUnOULGMOmpZ2cu2DmvCeq3Gh69aX1vW/T0lJK4qP1h6o5WfJa2WIn0+h9G4VadNtnpqbu2aH4J6Vb6VdULqr+ovLjeFVr+Ucz8QvDm74LpW9yqyuLZS5ev5NM9Q6LeUdR0uje2dTmt6sVKEvoanjvh2hxNwxc2dZdSok5Un2n6FvLi7sbz3C65nw8jWS26zPl5s4B45vOGNU/jVZ3GmVZYrUpPy/uPUug6jaatp9G6sa0atrKOYpPf/ACeNLuzq2N9XsbymlWoScaj7vudo9nHVaNKneaXUrylc1J88E3n3Srwc/n47u9/I+k47ceObi+3eoSyV5LccIrR09al8/wDom9inflbSy/QqmsrYxbqrVowcoxyks4JmdRtOnCPaz1+xpcE09DqT5dSuKsa0aeP2rzPLGh21G91/TLScc29WvCE4902snfPa20q81GppnEFGnixt4dCo36Sb2PPdnWuLO7tbm3SjXpyVSEvqvI9Z0jDvDMxPmVfJOpe/L/WeF/D7hW2hdXNK0020jGEacZJzi39D5xePPh/UkktWqfRdPGTxnqt5q3E2tydzUr32o3k0unFtrm2S2O4ab7MV5ccO0ru71Toao6Tn+lUU0pekcnJzcHFhnWa/lsi2/TtnD3ixwfxLq1Ow0fVl+qkm+WquWLS+rPvqbhNxqxnGcGvcnF5TPAnFPhjxdwfpMtU1ixVpbU5qn1YVMNt+XkdG8DPGiXDFKGicX1p1NLUc0K7950l2+phk6bSad+Ce6Cb6evopIlyS2NLoGuWOv6RQ1TTajqWNws0p4+I2ieZJzjh+hy5iYt2y2R5heJLbfoTvjYj70iJVgpZD8iY9bTHvSpvYxruqqdCc3JLC9WXpLEcZPkOO7+NvZ0reOc1d1g53UM/wYptLdx8fy5IpD5PW9SralcqFT3Ok39cl/hZ4163UWpc3n6GoouU6jnL40sG04ZnQt9Uo1ajxUi8Hz7BzL2zxMz9vT5+PXHhmKurR8kXEWqbUoJr1WS4vI+nYt/HDyM/2VPyKWVehT6m1LXan/PoSX7WGveS/q3Lt9R5qil2LSeduwAgAAAAAAADyBKxnfyAipl29ZJZcombbJq1pZ9Ili0xzVEuxgW365XSUHmhze99gMyxvXXuqlJxaUe6Nhn/sQlFPKSX+CX5AlElleRrOIr+Wk6HeX6ouu6FNz6cfOX0LuqfqnbwVntPO/wBjUcX31ey4T1G5oNO4oUHJKSyskWnxLZjr3TDyFrGoV9W1S6v6qnSjWqufRm8uO/kesPC3S7TSuD7OOn0Okq8FUmu7fqeSb2vVr1nXrYlcXMuefL5J5PZXAyxwlpXN5qhFFXBLt9Vj48NKt5EqaCJLjgfYSvIglECmo8RLc0uTMvJeZdZDWVgIfMcZcLWfFmjzs77en8VKX9Mu5yq18B3K+oy1DV3cUoy5nTSxzR7Hd4t5cEtijpyU8qWyNdsMWna7i6lyeLT4sdv1ljabZ0dN06lZWtPp0KK5YR+hF9fU7G0r3Nx7trRg5TaNDxXxvovDl3TtdWvP09SouaO3muxynxN8U6eq0HpvDNRdCcOWrcP/APOCMl4pX/jdwuDk5eWKUj25fxJd0tQ4n1G7tazqUbmo502+3Y+89nqxq1OL6t7GEejTg6ck5bp/Y5zpOlXWr3KtLJRV2veWXhSPRHhDwF/tumtSvnJ6nVXvQzhRX2ORxKTyM3fL3v8AIORTjdPjiTP7adTcWy5jYt/E8p7lcVnG/kdzu86fL5mZ8WTJ+7sUzl7qTjnJcBHvwyhxb2qaEpeFVWNOnNr9RCTUI5weMFL3JOcn5bI/RvjKCr8K6wpU41ErWpiLjzb8r9D85pU5rq89OVNqbzzLD8/LB6boWb+2Noyw9SeyvwHC106txFqlrRnd1P8ApanMpYj9V6M9DvqSlGXJu/XPwnnv2OK2eHtap1LlyqdZctOc94rHoux6JT5k0tseeTh9R7755i8tlIjT53j3h614r4WvtLvqP6pODlCm3jNRL3X+T8/uINFvdA1a60zV6E6F1Sk4tSW3+D9IOZJtvecdtvU8re2TCnDiDQHGlDnlRk5SSw3v6l3pXJmmT449SjJDoHst8XQ1jgpaRKhTt3peKafNvUz6pHaKkoUf4lSTb9DxR7L85f8AFyyhGc4w6M8xT93y9Ue26qU4yhNYTfmV+o44xZ5iGVJ3BSqwrx5ol1ZMehbwt9ol+RQ15I9qiMkJFLWG2Ra2krNzNwp1JRe6Wf8ABy7WtTnqeoL3fcg3GOT7nibV6enWrhPapUTUTn2l28L6/wAVJNW0HmbSPJdbzzyJjFjdjpdIw7y3ZmrWH6Gwt5x3nU3cl5GuhGLrUEn/ABJSTbPueIKVtR4XatlmkvJvdnwCyqadN+6eb5/E/DzUdThZPyMVu52LT5c9vTw08RW6MtGi4Nb/ANDoNtt/U3kfM+k8TL34ay8vmr25JhX6FLKgWmtarv8AhMwI7ZZsLj+WzAj6gQAAAAAAAASvoQAL9rj+IvXBOnZ6c98rIsvjn9iNOeKNVr+tgZaEt00P2pseaRBK3JYaktvRnLvaA1S80rhGjCzmo07ur0qz/tOozWYe8stPZHIPaQ5XwhZdafJivlQ/qMLT+srXEjeSv/XnSEVStqkKTc5Ke0n2yez+B8/7O0jv0I5PGM5SlRdSC6cE0nT779z2VwY0uEtIaXuu3i8dtivx6y63W7RaK1j6fRrzJLNN5fu/CXG/e8y689We5USgCEoY9CGQ37rCJ8eVE58uNvMx7urC2ipVJYUvMrdTMG5NQUfVsxK1fT76HRlcUZT/AKVNDuiseU4om098emi4m4L0Hi2rTu9WtetOnHlhLPoc+4q8FrOrGVxoknRjCD5bb+qXc7RQhGEFRjHlUV7pRd1advTjUrP+3Y0/F8k+V7i9Qy8O/djnTxbVp3mi6nJXcalK6tZYS+HEl/5O0+DPiJqOr6s9I19xqVZrnpV2+XC7GP7QvD9KVO116nyuKxTdOKxn6s43ZXk7HUKF83LFrNSUYvD2OTfu4uf9fT6FbFTr/Tvn1+8PbdNYmXcbmk4O1eOucP2OocuJVqaZu0syz2O5Wdx3S+Y5cdq3mt/pWiGTgMemMLOyypRXK3hp75PC3tAaFe6H4lanXvbeFG3vqjq2vJsnH7HuuSWMvaTONe0rwLLifhJ3+n2Lu9as8ckk8ctPzlsX+m8j8bNF/pjaNw8+ez7xFZcPeJtnd6pdTpW1WDo8sfJzlsso9wqHPF88nKm/eXpg/NiEq1hc0akoTt7qlJTg6kMYkvoz2D4E+Mdvxdaw0XXpQttbowwpyeFXSW7LvVsG7/LXzEsKS7TFqTWFhtbHkH2s9dsNV4xsbGzrOVxp8HTuNvhkejOM/Evhzg5qnq9/SVzKm50YQfNz49PoeI+MdXuON+NL/VbGxqKd9VxGFOPNu3hZNPSsXZl+W3iITfy6N7KWiajecfrV6FFS023hKFepneMmtj2ZJyUmmubL2RzbwK4MjwfwPbUqtq7TVrmKneLOcy9P+x0mHpJvMilzcvzZZuzrHbCvbzZHxeROAVZ8p9DeEY93VVKhObaWE3uVrLbR8jx9fq3o07ffNbsUuflthxzOmfHrGW8Q+X1m+q6peLqRcnFuNNI+z4a0mnbaTTp1qCc57zZ8Lpl5Gwv4SaVRJep9JDjadNOKs1JL+48h0/k4vyJyZZdvnUtOKMeOPDc8XwjQ4enClH+EvQ5xjEcr4MH0Wt8Uf6lYToTodOL7PJ81GXvcsVmMl6lH+QcrHyc0TjnxC70vDfFimtodS4N30Khj6m78sHNtN4kqaZpdOhSpJuP1PpuF9bqarCoqtNR5Pqet6T1HDfDWke3B5XGvTJNpfTryBbU8laZ3dqSi4/lswI+pnV5fw2YEd8iJ2AAJAAAAAAAAGRZfHP7Eab/Jq/8AzYsvimvoU6TPnp1X2m0BmPPkWbm6p26XUeEXvJ5ZZuLWnc/ED6SpOpTTpy2aypHz3GXC9txVos7G/wDeqLenU/ofc+jhBU4KEfJFxeW4mDHeazuHBbDwH6F3Slfaz+osoT5qlFRw5Ltk7JRsKdtp8LS1bp2kaap0o+qNq0uyJSX0FaxX023z2y/2lhaXbStaKhOo5szN1L4f8lSSJEtXoAIk8JgRIobwnvgqeWosolHnk09tgOYePeqX2mcIqenVXCdafTm1t7p5tpahf2UqVza31eVxSlmMed/9z07406DW1zhGqqdV0naPq4Sz1EvQ8t0Z0q1WKuJOzoTfLUk1lx+pyebOT5Y16fQ/4xOC3DvW+tvY/A17X1ThXTrq9l/zE6SlI3V3TjXpKMoc0c5PmvD9WlHhvTqFnfQu4qkuSae7X2N/qF9bWFrUubiooQprMm3tg6Nb9tHheRhic1uyPcuW+0K4w4MhTotRmqqeM7/g86XDX6Tmc8Se0vqfaeKXF0eLOIXXtOenY2/8PkztP+4wPDzhlcWcRw09VXC1j78542a7HIyZJzZIfTOk0t0rpdrZvG/L014XrHA+k8iSiqXofWxWFv3Ndoum0tJ063srKLhbUo4WWbFZznOx2qRusPl/ImL5bZN+1z0KZfC/QkkTXbUsPZKS96fct06csyjUfPGXr/8Awy8IhkREwPgeOfDHhnjD+JqenQV5Cm6dKtTXKot+TaXmcep+y9Wo1FOjxNKnUWf4kYtM9NpFWCzh5mbHH6SwiHmb/wBMVW4vKFXUuJ53dKMk5Kabbj6pNnaODuAeHOD6FanoOmQiqkk6jqJSba9U35H2OCUjG/KyZZ/dlrTHp8/UkorlhHv6lyMVluPuyfmXWUM0XmZlKv0JZRj3ov0RTCSlKXLJMy9QKeZ+9hGv1HS7bUqlOdzHLp7I2iJwYXxRljVvKa5NeYh81/tfTpVajdLcuf7U0rDzRe/1N9L3vJ4KVF/ueSjHS+LSdzDd+Re8e3xnEWgWGnaRVqUKTg0/NvJ8ZN81LGE+U6TxinLQbhYy8o5nHM8xlHGx4br2HHx+RFcfp3+kzObDM2fb8NaFp17pNK4qUm3Lz3N/puk2li6krem0pfUw+DJJ6HRgnjGdjexTzJcvL9e57LpXH4/wVtT24fJvf5ZrK6o7Iq8jAvLyVtUjCKbTMunPmpKbW7OvKqprx/hsw4eTM6t/KMFeoqAAMgAAAAAAA/IC5Sn06VSp2RdsaapU+WP7/eMack6dSH9SwjPox5aNNdlgCrO2GTFFQBHhSlvkp5m5fQuEbE7I8IkUJ7mL+rcrrpRi8IzX9CEaSgQvqsE+eMBKSPUkAa67v1RuoUuV4f0M57rmXqipwi3lxTf2JAw60IXEHCUOaPk0/U5bx74SadqWn3dfSaat9Qm3UXaX0wdeSS8kGk/MxtWLe1jjcrLx5iaS8naBwt4haJcSqaXa1aNdLl5nPMeX6I2euaZ4naxYKzu6Dq0Xu3F4yz05049iVBLyNMYNeN+HRydZvktF5rG4eeNA8Eb2ULW6vb6NFzX8ehj8o67wjwdpPDNvOhpduoqUuaUn55+59XgJJeSMq4aV9Q0cvq/K5cduW/j/ABYhnEuo9s7Ff7lFR93uXMIG5zNQAAjykIJA0KGEVgn6I8IBIMYifsQyib5Yyl2WS4CZgamheVL6jcKnFxcdlknSLedGm51qnM2/RmwlTThUjFKOV6GFplm7OFRyk5cz9XkmfIzvMlZRMSpkdsETP2svffB8xxHd63SljTKHMj60YXY1ZMU3jW2VJrWd6c1v48R6hbKnUtXiXnuYS4a1RRzK3fN9zqqgktiGlny2OHn6Biz27rSv06jkxxqniHN7O04isqfSt7fmS/dk+s0SjfugpalV97+k3qiS4pl7jdNpx41WVfJybZPMrMqcZNOcc4Li3WEsIrfkUnT0rLdb+UYK9TKu6qhyx7mIlhzYiNCQASAAAAAAAALNXavQ/ulg26ef8PBqpJuvSws4eTbR3W4FQIRIAgkAW40oRnzpLmfqW7l1Y05uksyxsZAAxLB3Dpr9SsMyvsSAAAAAAAAAGw2DKfUCoEb+edg9t16gSAAAAAAAAAAAAAAACM7ZI8tmHlfYx6N1Rq1XTg8yQGREqZCJAAACiTcVOXZZMHTr13iqR5WnF+qNiRGMY/DFL7ICIlQABlPqVMp9QNdqWf1FMPdyRVfe9cQS/b5lH7ogAS/MgAAAAAAB+QAGRbRTqbryWTKz77X0NdmovgmslLq3eNqkcAbVA1HVuvnL8Dq3Xzl+ANuDUdW6+cvwOrdfOX4A24NR1br5y/A6t185fgDbg1HVuvnL8Dq3Xzl+ANuDUdW6+cvwOrdfOX4A24NR1br5y/A6t185fgDbg1HVuvnL8Dq3Xzl+ANuylrzz5Gq6t185fgdW69ay/AGdTuqMqroxfvIvrOcY2NFStq0K7rqtHL+hkqrdNt9VY+wG2BqOrdfOX4HVuvnL8AbcGo6t185fgdW6+cvwBtwajq3Xzl+B1br5y/AG3BqOrdfOX4HVuvnL8AbcGo6t185fgdW6+cvwBtwajq3Xzl+B1br5y/AG1ll/YxqFnRo1nVh8TMPq3Xzl+B1Lr5q/AG2RJqOrdfOX4HVuvnL8AbcGo6t185fgdW6+cvwBtwajq3Xzl+B1br5y/AG3BqOrdfOX4J6t181fgDbMp9TXdS6+YiOpdY+NAVZ57y5T/atileSZVTeKLz8T82Ux2gAAAAAAAAAAADZ/taJ5V55eQQBO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABLedlJYDk37sWtiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO/dDfuiABO31G31IAEJ42JlsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//Z";

/*    public static void main(String[] args)  	{
        test();
    }*/

    public static void test(){
       stringToBitmap(tt);
   }

   public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }
    public static void decoderBase64File(String base64Code,String savePath) throws Exception {
        //byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        byte[] buffer =Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

    public Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


   /* public String bitmaptoString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    public static boolean generateImage(String imgStr, String path) {
        if (imgStr == null)
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
// 解密
            byte[] b = decoder.decodeBuffer(imgStr);
// 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
        //图片转化成base64字符串
        public static String GetImageStr()
        {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
            String imgFile = "F:\\tupian\\a.jpg";//待处理的图片
            // 地址也有写成"F:/deskBG/86619-107.jpg"形式的
            InputStream in = null;
            byte[] data = null;
            //读取图片字节数组
            try
            {
                in = new FileInputStream(imgFile);
                data = new byte[in.available()];
                in.read(data);
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(data);//返回Base64编码过的字节数组字符串
        }*/



}
