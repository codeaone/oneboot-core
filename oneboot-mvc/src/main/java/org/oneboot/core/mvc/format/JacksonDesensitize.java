//package org.oneboot.core.mvc.format;
//
//import java.io.IOException;
//import java.util.Objects;
//
//import org.oneboot.core.logging.sensitive.Sensitive;
//import org.oneboot.core.logging.sensitive.SensitiveDataUtil;
//import org.oneboot.core.logging.sensitive.SensitiveTypeEnum;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.BeanProperty;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.ContextualSerializer;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @JsonSerialize(using=JacksonDesensitize.class) TODO 可不可以默认过来看下？
// * @author shiqiao.pro
// * 
// */
//@Slf4j
//public class JacksonDesensitize extends JsonSerializer<String> implements ContextualSerializer {
//
//	private Sensitive sensitive;
//
//	private SensitiveTypeEnum typeEnum;
//
//	public JacksonDesensitize(final SensitiveTypeEnum typeEnum) {
//		this.typeEnum = typeEnum;
//	}
//
//	@Override
//	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
//			throws JsonMappingException {
//		if (property != null) {
//			if (Objects.equals(property.getType().getRawClass(), String.class)) { // 非 String 类直接跳过
//				sensitive = property.getAnnotation(Sensitive.class);
//				if (sensitive == null) {
//					// 这样写有什么用 ？ TODO
//					sensitive = property.getContextAnnotation(Sensitive.class);
//				}
//				// 如果能得到注解，就将注解的 value 传入 JacksonDesensitize
//				if (sensitive != null) {
//					return new JacksonDesensitize(sensitive.type());
//				}
//			}
//			return prov.findValueSerializer(property.getType(), property);
//		}
//		return this;
//	}
//
//	@Override
//	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//		switch (this.typeEnum) {
//
//		case CLEAN:
//
//			break;
//		case ALL:
//
//			break;
//		case BEGIN6_END4:
//
//			break;
//		case PASSWORD:
//
//			break;
//		case BANK_CARD_NO:
//			gen.writeString(SensitiveDataUtil.bankCardNoHide(value));
//			break;
//		case MOBILE_NO:
//			gen.writeString(SensitiveDataUtil.mobileHide(value));
//			break;
//		case ID_CARD_NO:
//			gen.writeString(SensitiveDataUtil.idCardNoHide(value));
//			break;
//		case PERSON_NAME:
//
//			break;
//
//		default:
//			log.info("SensitiveTypeEnum注解类型没有找到");
//			break;
//		}
//	}
//
//}
