package ru.clevertec.cleverbank.dao.codec;

import org.bson.BsonDecimal128;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.cleverbank.builder.nbrbcurrency.NbRBCurrencyTestBuilder;
import ru.clevertec.cleverbank.model.NbRBCurrency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NbRBCurrencyCodecTest {

    @InjectMocks
    private NbRBCurrencyCodec codec;
    @Mock
    private CodecRegistry codecRegistry;
    @Mock
    private BsonWriter writer;
    @Mock
    private EncoderContext encoderContext;
    @Mock
    private BsonReader reader;
    @Mock
    private DecoderContext decoderContext;
    @Mock
    private Codec<BsonDocument> bsonDocumentCodec;
    @Captor
    private ArgumentCaptor<BsonDocument> captor;

    @Test
    @DisplayName("test encode should capture expected BsonDocument")
    void testEncodeShouldCaptureExpectedBsonDocument() {
        NbRBCurrency nbRBCurrency = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
        BsonDocument expected = new BsonDocument()
                .append("currency_id", new BsonInt32(nbRBCurrency.getCurrencyId()))
                .append("currency", new BsonString(nbRBCurrency.getCurrency().name()))
                .append("scale", new BsonInt32(nbRBCurrency.getScale()))
                .append("rate", new BsonDecimal128(Decimal128.parse(nbRBCurrency.getRate().toString())))
                .append("update_date", new BsonString(nbRBCurrency.getUpdateDate().toString()));

        doReturn(bsonDocumentCodec)
                .when(codecRegistry)
                .get(BsonDocument.class);
        doNothing()
                .when(bsonDocumentCodec)
                .encode(writer, expected, encoderContext);

        codec.encode(writer, nbRBCurrency, encoderContext);

        verify(bsonDocumentCodec).encode(eq(writer), captor.capture(), eq(encoderContext));

        BsonDocument actual = captor.getValue();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test decode should return expected NbRBCurrency")
    void testDecodeShouldReturnExpectedNbRBCurrency() {
        NbRBCurrency expected = NbRBCurrencyTestBuilder.aNbRBCurrency().build();
        BsonDocument bsonDocument = new BsonDocument()
                .append("_id", new BsonObjectId(expected.getId()))
                .append("currency_id", new BsonInt32(expected.getCurrencyId()))
                .append("currency", new BsonString(expected.getCurrency().name()))
                .append("scale", new BsonInt32(expected.getScale()))
                .append("rate", new BsonDecimal128(Decimal128.parse(expected.getRate().toString())))
                .append("update_date", new BsonString(expected.getUpdateDate().toString()));

        doReturn(bsonDocumentCodec)
                .when(codecRegistry)
                .get(BsonDocument.class);
        doReturn(bsonDocument)
                .when(bsonDocumentCodec)
                .decode(reader, decoderContext);

        NbRBCurrency actual = codec.decode(reader, decoderContext);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test getEncoderClass should return expected class")
    void testGetEncoderClassShouldReturnExpectedClass() {
        Class<NbRBCurrency> expectedClass = NbRBCurrency.class;

        Class<NbRBCurrency> actualClass = codec.getEncoderClass();

        assertThat(actualClass).isEqualTo(expectedClass);
    }

}
