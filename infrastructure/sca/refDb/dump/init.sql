-- Database: siritDB
CREATE TABLE sirit
(
    itemId SERIAL PRIMARY KEY,
    bucketId bigint,
    segmentId bigint,
    deudorId bigint,
    estrategiaId bigint,
    deudorName VARCHAR(50),
    deudorState VARCHAR(50),
    lastUpdateBy bigint,
    lastUpdateAction VARCHAR(50)
    
);
CREATE TABLE segmentStrategies
(
    segmentStrategyId SERIAL PRIMARY KEY,
    segmentId bigint UNIQUE,
    nombreEstrategia VARCHAR(50),
    mensajes json    
);

-- Estrategia "No contactado"
INSERT INTO segmentStrategies (segmentId, nombreEstrategia, mensajes)
VALUES
(11, 'No contactado', '[{
    "tipo": "SMS",
    "cuerpo": "Estimado cliente, intentamos ponernos en contacto con usted pero no hemos recibido respuesta. Por favor, comuníquese con nosotros."
},{
    "tipo": "FAX",
    "cuerpo": "Estimado cliente, intentamos ponernos en contacto con usted por fax. Por favor, comuníquese con nosotros."
},{
    "tipo": "EMAIL",
    "cuerpo": "Estimado cliente, intentamos ponernos en contacto con usted por correo electrónico pero no hemos recibido respuesta. Por favor, comuníquese con nosotros."
}]')
ON CONFLICT (segmentId) DO UPDATE
SET nombreEstrategia = EXCLUDED.nombreEstrategia,
    mensajes = EXCLUDED.mensajes;

-- Estrategia "Acuerdo de pago"
INSERT INTO segmentStrategies (segmentId, nombreEstrategia, mensajes)
VALUES
(21, 'Acuerdo de pago', '[{
    "tipo": "SMS",
    "cuerpo": "Estimado cliente, le recordamos que su acuerdo de pago está pendiente. Por favor, realice el pago correspondiente."
},{
    "tipo": "FAX",
    "cuerpo": "Estimado cliente, le recordamos que su acuerdo de pago está pendiente. Por favor, realice el pago correspondiente."
},{
    "tipo": "EMAIL",
    "cuerpo": "Estimado cliente, le recordamos que su acuerdo de pago está pendiente. Por favor, realice el pago correspondiente."
},{
    "tipo": "PHONE",
    "cuerpo": "Estimado cliente, le recordamos que su acuerdo de pago está pendiente. Por favor, realice el pago correspondiente."
}]')
ON CONFLICT (segmentId) DO UPDATE
SET nombreEstrategia = EXCLUDED.nombreEstrategia,
    mensajes = EXCLUDED.mensajes;

-- Estrategia "Sin acuerdo/negativa"
INSERT INTO segmentStrategies (segmentId, nombreEstrategia, mensajes)
VALUES
(31, 'Sin acuerdo/negativa', '[{
    "tipo": "SMS",
    "cuerpo": "Estimado cliente, lamentamos informarle que no hemos llegado a un acuerdo. No se podrá realizar la acción solicitada."
},{
    "tipo": "FAX",
    "cuerpo": "Estimado cliente, lamentamos informarle que no hemos llegado a un acuerdo. No se podrá realizar la acción solicitada."
},{
    "tipo": "EMAIL",
    "cuerpo": "Estimado cliente, lamentamos informarle que no hemos llegado a un acuerdo. No se podrá realizar la acción solicitada."
}]')
ON CONFLICT (segmentId) DO UPDATE
SET nombreEstrategia = EXCLUDED.nombreEstrategia,
    mensajes = EXCLUDED.mensajes;

-- Estrategia "Incumplimiento de pago"
INSERT INTO segmentStrategies (segmentId, nombreEstrategia, mensajes)
VALUES
(41, 'Incumplimiento de pago', '[{
    "tipo": "SMS",
    "cuerpo": "Estimado cliente, su pago no ha sido realizado a tiempo. Por favor, regularice la situación lo antes posible."
},{
    "tipo": "FAX",
    "cuerpo": "Estimado cliente, su pago no ha sido realizado a tiempo. Por favor, regularice la situación lo antes posible."
},{
    "tipo": "EMAIL",
    "cuerpo": "Estimado cliente, su pago no ha sido realizado a tiempo. Por favor, regularice la situación lo antes posible."
}]')
ON CONFLICT (segmentId) DO UPDATE
SET nombreEstrategia = EXCLUDED.nombreEstrategia,
    mensajes = EXCLUDED.mensajes;