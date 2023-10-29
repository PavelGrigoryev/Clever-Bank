package ru.clevertec.cleverbank.dao.codec;

import lombok.AllArgsConstructor;
import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.Decimal128;
import ru.clevertec.cleverbank.model.Currency;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import java.time.LocalDateTime;

@AllArgsConstructor
public class NbRBCurrencyCodec implements Codec<NbRBCurrency> {

    private final CodecRegistry codecRegistry;

    public NbRBCurrencyCodec() {
        codecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().register(NbRBCurrency.class).build(),
                new BsonValueCodecProvider());
    }

    @Override
    public void encode(BsonWriter writer, NbRBCurrency value, EncoderContext encoderContext) {
        BsonDocument document = new BsonDocument()
                .append("currency_id", new BsonInt32(value.getCurrencyId()))
                .append("currency", new BsonString(value.getCurrency().name()))
                .append("scale", new BsonInt32(value.getScale()))
                .append("rate", new BsonDecimal128(Decimal128.parse(value.getRate().toString())))
                .append("update_date", new BsonString(value.getUpdateDate().toString()));
        codecRegistry.get(BsonDocument.class).encode(writer, document, encoderContext);
    }

    @Override
    public NbRBCurrency decode(BsonReader reader, DecoderContext decoderContext) {
        BsonDocument document = codecRegistry.get(BsonDocument.class).decode(reader, decoderContext);
        return NbRBCurrency.builder()
                .id(document.getObjectId("_id").getValue())
                .currencyId(document.getInt32("currency_id").getValue())
                .currency(Currency.valueOf(document.getString("currency").getValue()))
                .scale(document.getInt32("scale").getValue())
                .rate(document.getDecimal128("rate").decimal128Value().bigDecimalValue())
                .updateDate(LocalDateTime.parse(document.getString("update_date").getValue()))
                .build();
    }

    @Override
    public Class<NbRBCurrency> getEncoderClass() {
        return NbRBCurrency.class;
    }

}
