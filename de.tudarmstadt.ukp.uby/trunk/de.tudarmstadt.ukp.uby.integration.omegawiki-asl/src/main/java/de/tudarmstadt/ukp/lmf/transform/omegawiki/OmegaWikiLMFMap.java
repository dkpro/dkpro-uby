/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tudarmstadt.ukp.lmf.transform.omegawiki;

import java.util.HashMap;

import de.tudarmstadt.ukp.lmf.model.enums.ELanguageIdentifier;

/*
 * Maps OmegaWiki constants to LMF constants
 * @author Yevgen Chebotar
 * @author Christian M. Meyer
 */
public class OmegaWikiLMFMap {

	private static HashMap<Integer, String> languageNames = new HashMap<Integer, String>();  // Language maps from Wiktionary to LMF
	private static String langCode = "language_code.en";

	/**
	 * Load language codes from the given path
	 * create language mappings from Wiktionary to LMF
	 * @param path
	 */






	/**
	 * Maps Wiktionary Language to LMF LanguageIdentifier
	 * http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
	 * @param lang
	 */
	public static String mapLanguage(int lang){
		languageNames.put(84,ELanguageIdentifier.ISO639_BUL);
		languageNames.put(85,ELanguageIdentifier.ISO639_ENG);
		languageNames.put(86,ELanguageIdentifier.ISO639_FRA);
		languageNames.put(87,ELanguageIdentifier.ISO639_SPA);
		languageNames.put(88,ELanguageIdentifier.ISO639_RUS);
		languageNames.put(89,ELanguageIdentifier.ISO639_NLD);
		languageNames.put(90,ELanguageIdentifier.ISO639_CES);
		languageNames.put(91,ELanguageIdentifier.ISO639_SWE);
		languageNames.put(92,ELanguageIdentifier.ISO639_SLV);
		languageNames.put(93,ELanguageIdentifier.ISO639_POL);
		languageNames.put(94,ELanguageIdentifier.ISO639_POR);
		languageNames.put(95,ELanguageIdentifier.ISO639_NOB);
		languageNames.put(96,ELanguageIdentifier.ISO639_EUS);
		languageNames.put(97,ELanguageIdentifier.ISO639_SLK);
		languageNames.put(98,ELanguageIdentifier.ISO639_EST);
		languageNames.put(99,ELanguageIdentifier.ISO639_FIN);
		languageNames.put(100,ELanguageIdentifier.ISO639_ITA);
		languageNames.put(101,ELanguageIdentifier.ISO639_DEU);
		languageNames.put(102,ELanguageIdentifier.ISO639_HUN);
		languageNames.put(103,ELanguageIdentifier.ISO639_DAN);
		languageNames.put(104,ELanguageIdentifier.ISO639_ENG);
		languageNames.put(105,ELanguageIdentifier.ISO639_ELL);
		languageNames.put(106,ELanguageIdentifier.ISO639_HEB);
		languageNames.put(107,ELanguageIdentifier.ISO639_ZHO);
		languageNames.put(109,ELanguageIdentifier.ISO639_NAP);
		languageNames.put(110,ELanguageIdentifier.ISO639_MAR);
		languageNames.put(111,ELanguageIdentifier.ISO639_LMO);
		languageNames.put(112,ELanguageIdentifier.ISO639_JPN);
		languageNames.put(113,ELanguageIdentifier.ISO639_EPO);
		languageNames.put(114,ELanguageIdentifier.ISO639_PMS);
		languageNames.put(115,ELanguageIdentifier.ISO639_VEC);
		languageNames.put(116,ELanguageIdentifier.ISO639_UKR);
		languageNames.put(117,ELanguageIdentifier.ISO639_CAT);
		languageNames.put(118,ELanguageIdentifier.ISO639_EWE);
		languageNames.put(119,ELanguageIdentifier.ISO639_AKA);
		languageNames.put(120,ELanguageIdentifier.ISO639_ARB);
		languageNames.put(121,ELanguageIdentifier.ISO639_HAU);
		languageNames.put(122,ELanguageIdentifier.ISO639_HAU);
		languageNames.put(123,ELanguageIdentifier.ISO639_HYE);
		languageNames.put(124,ELanguageIdentifier.ISO639_KAT);
		languageNames.put(125,ELanguageIdentifier.ISO639_AFR);
		languageNames.put(126,ELanguageIdentifier.ISO639_FRA);
		languageNames.put(127,ELanguageIdentifier.ISO639_FRA);
		languageNames.put(128,ELanguageIdentifier.ISO639_SRP);
		languageNames.put(129,ELanguageIdentifier.ISO639_SRP);
		languageNames.put(130,ELanguageIdentifier.ISO639_THA);
		languageNames.put(131,ELanguageIdentifier.ISO639_IDO);
		languageNames.put(133,ELanguageIdentifier.ISO639_RON);
		languageNames.put(134,ELanguageIdentifier.ISO639_KSH);
		languageNames.put(135,ELanguageIdentifier.ISO639_ZHO);
		languageNames.put(136,ELanguageIdentifier.ISO639_ENG);
		languageNames.put(137,ELanguageIdentifier.ISO639_TUR);
		languageNames.put(138,ELanguageIdentifier.ISO639_KOR);
		languageNames.put(139,ELanguageIdentifier.ISO639_KHM);
		languageNames.put(140,ELanguageIdentifier.ISO639_SCN);
		languageNames.put(141,ELanguageIdentifier.ISO639_ARG);
		languageNames.put(142,ELanguageIdentifier.ISO639_PES);
		languageNames.put(143,ELanguageIdentifier.ISO639_LAV);
		languageNames.put(144,ELanguageIdentifier.ISO639_VIE);
		languageNames.put(145,ELanguageIdentifier.ISO639_SWH);
		languageNames.put(146,ELanguageIdentifier.ISO639_BAR);
		languageNames.put(147,ELanguageIdentifier.ISO639_BEL);
		languageNames.put(148,ELanguageIdentifier.ISO639_CSB);
		languageNames.put(149,ELanguageIdentifier.ISO639_HRV);
		languageNames.put(150,ELanguageIdentifier.ISO639_LIT);
		languageNames.put(151,ELanguageIdentifier.ISO639_YUE);
		languageNames.put(152,ELanguageIdentifier.ISO639_TGK);
		languageNames.put(153,ELanguageIdentifier.ISO639_CYM);
		languageNames.put(154,ELanguageIdentifier.ISO639_BAM);
		languageNames.put(155,ELanguageIdentifier.ISO639_POR);
		languageNames.put(156,ELanguageIdentifier.NONSTD_POR_BR);
		languageNames.put(158,ELanguageIdentifier.ISO639_TEL);
		languageNames.put(159,ELanguageIdentifier.ISO639_GLK);
		languageNames.put(160,ELanguageIdentifier.ISO639_MHR);
		languageNames.put(161,ELanguageIdentifier.ISO639_MRJ);
		languageNames.put(162,ELanguageIdentifier.ISO639_GUJ);
		languageNames.put(163,ELanguageIdentifier.ISO639_HIN);
		languageNames.put(164,ELanguageIdentifier.ISO639_TAM);
		languageNames.put(165,ELanguageIdentifier.ISO639_IND);
		languageNames.put(166,ELanguageIdentifier.ISO639_GLE);
		languageNames.put(167,ELanguageIdentifier.ISO639_FRA);
		languageNames.put(168,ELanguageIdentifier.ISO639_LIM);
		languageNames.put(169,ELanguageIdentifier.ISO639_GSW);
		languageNames.put(170,ELanguageIdentifier.ISO639_RWR);
		languageNames.put(171,ELanguageIdentifier.ISO639_SRN);
		languageNames.put(172,ELanguageIdentifier.ISO639_DEU);
		languageNames.put(173,ELanguageIdentifier.ISO639_DEU);
		languageNames.put(174,ELanguageIdentifier.ISO639_GRC);
		languageNames.put(175,ELanguageIdentifier.ISO639_GMY);
		languageNames.put(176,ELanguageIdentifier.ISO639_CPG);
		languageNames.put(177,ELanguageIdentifier.ISO639_ALS);
		languageNames.put(178,ELanguageIdentifier.ISO639_ALN);
		languageNames.put(179,ELanguageIdentifier.ISO639_AAT);
		languageNames.put(180,ELanguageIdentifier.ISO639_AAE);
		languageNames.put(181,ELanguageIdentifier.ISO639_KAN);
		languageNames.put(182,ELanguageIdentifier.ISO639_BRE);
		languageNames.put(183,ELanguageIdentifier.NONSTD_FRI);
		languageNames.put(184,ELanguageIdentifier.ISO639_NAV);
		languageNames.put(185,ELanguageIdentifier.ISO639_BEN);
		languageNames.put(186,ELanguageIdentifier.ISO639_URD);
		languageNames.put(187,ELanguageIdentifier.ISO639_FRA);
		languageNames.put(188,ELanguageIdentifier.ISO639_NEP);
		languageNames.put(189,ELanguageIdentifier.ISO639_IBO);
		languageNames.put(190,ELanguageIdentifier.ISO639_LLD);
		languageNames.put(191,ELanguageIdentifier.ISO639_KAZ);
		languageNames.put(192,ELanguageIdentifier.ISO639_WLN);
		languageNames.put(193,ELanguageIdentifier.ISO639_ISL);
		languageNames.put(194,ELanguageIdentifier.ISO639_TAT);
		languageNames.put(195,ELanguageIdentifier.ISO639_AST);
		languageNames.put(196,ELanguageIdentifier.ISO639_SND);
		languageNames.put(197,ELanguageIdentifier.ISO639_SND);
		languageNames.put(198,ELanguageIdentifier.ISO639_YOR);
		languageNames.put(199,ELanguageIdentifier.ISO639_GLG);
		languageNames.put(200,ELanguageIdentifier.ISO639_NNO);
		languageNames.put(202,ELanguageIdentifier.ISO639_LIN);
		languageNames.put(203,ELanguageIdentifier.ISO639_XHO);
		languageNames.put(204,ELanguageIdentifier.ISO639_HSB);
		languageNames.put(205,ELanguageIdentifier.ISO639_DSB);
		languageNames.put(206,ELanguageIdentifier.ISO639_TGL);
		languageNames.put(207,ELanguageIdentifier.ISO639_MLT);
		languageNames.put(208,ELanguageIdentifier.ISO639_CEB);
		languageNames.put(209,ELanguageIdentifier.ISO639_MKD);
		languageNames.put(210,ELanguageIdentifier.ISO639_INA);
		languageNames.put(211,ELanguageIdentifier.ISO639_ILE);
		languageNames.put(213,ELanguageIdentifier.ISO639_FAO);
		languageNames.put(214,ELanguageIdentifier.ISO639_ZUL);
		languageNames.put(215,ELanguageIdentifier.ISO639_SAN);
		languageNames.put(216,ELanguageIdentifier.ISO639_NEW);
		languageNames.put(217,ELanguageIdentifier.ISO639_MAL);
		languageNames.put(218,ELanguageIdentifier.ISO639_VLS);
		languageNames.put(219,ELanguageIdentifier.ISO639_CIC);
		languageNames.put(220,ELanguageIdentifier.ISO639_COS);
		languageNames.put(221,ELanguageIdentifier.ISO639_VOL);
		languageNames.put(222,ELanguageIdentifier.ISO639_MAL);
		languageNames.put(223,ELanguageIdentifier.ISO639_KAB);
		languageNames.put(224,ELanguageIdentifier.ISO639_GLV);
		languageNames.put(225,ELanguageIdentifier.ISO639_OSS);
		languageNames.put(226,ELanguageIdentifier.ISO639_CRH);
		languageNames.put(227,ELanguageIdentifier.ISO639_BAK);
		languageNames.put(228,ELanguageIdentifier.ISO639_CHR);
		languageNames.put(229,ELanguageIdentifier.ISO639_KIR);
		languageNames.put(230,ELanguageIdentifier.ISO639_GIL);
		languageNames.put(231,ELanguageIdentifier.ISO639_JAV);
		languageNames.put(232,ELanguageIdentifier.ISO639_EXT);
		languageNames.put(233,ELanguageIdentifier.ISO639_TET);
		languageNames.put(234,ELanguageIdentifier.ISO639_MWL);
		languageNames.put(235,ELanguageIdentifier.NONSTD_EML);
		languageNames.put(236,ELanguageIdentifier.ISO639_WOL);
		languageNames.put(237,ELanguageIdentifier.ISO639_SOM);
		languageNames.put(238,ELanguageIdentifier.ISO639_LAT);
		languageNames.put(239,ELanguageIdentifier.ISO639_NOV);
		languageNames.put(240,ELanguageIdentifier.ISO639_YDD);
		languageNames.put(241,ELanguageIdentifier.ISO639_PJT);
		languageNames.put(242,ELanguageIdentifier.ISO639_JBO);
		languageNames.put(243,ELanguageIdentifier.ISO639_LTZ);
		languageNames.put(244,ELanguageIdentifier.ISO639_ROH);
		languageNames.put(245,ELanguageIdentifier.ISO639_XMF);
		languageNames.put(246,ELanguageIdentifier.ISO639_MRI);
		languageNames.put(247,ELanguageIdentifier.ISO639_KAA);
		languageNames.put(248,ELanguageIdentifier.ISO639_DIV);
		languageNames.put(249,ELanguageIdentifier.ISO639_ASM);
		languageNames.put(250,ELanguageIdentifier.ISO639_ABK);
		languageNames.put(251,ELanguageIdentifier.ISO639_AVK);
		languageNames.put(252,ELanguageIdentifier.ISO639_SUN);
		languageNames.put(253,ELanguageIdentifier.ISO639_COR);
		languageNames.put(254,ELanguageIdentifier.ISO639_PDC);
		languageNames.put(255,ELanguageIdentifier.ISO639_SWB);
		languageNames.put(256,ELanguageIdentifier.ISO639_STQ);
		languageNames.put(257,ELanguageIdentifier.ISO639_LUG);
		languageNames.put(258,ELanguageIdentifier.ISO639_SIN);
		languageNames.put(259,ELanguageIdentifier.ISO639_BPY);
		languageNames.put(260,ELanguageIdentifier.ISO639_CKB);
		languageNames.put(261,ELanguageIdentifier.ISO639_TIR);
		languageNames.put(262,ELanguageIdentifier.ISO639_GLA);
		languageNames.put(263,ELanguageIdentifier.ISO639_ANG);
		languageNames.put(264,ELanguageIdentifier.ISO639_DZO);
		languageNames.put(265,ELanguageIdentifier.ISO639_LFN);
		languageNames.put(266,ELanguageIdentifier.ISO639_YRL);
		languageNames.put(267,ELanguageIdentifier.ISO639_SZL);
		languageNames.put(268,ELanguageIdentifier.ISO639_RUQ);
		languageNames.put(269,ELanguageIdentifier.ISO639_INH);
		languageNames.put(270,ELanguageIdentifier.ISO639_BCC);
		languageNames.put(271,ELanguageIdentifier.ISO639_MYV);
		languageNames.put(272,ELanguageIdentifier.ISO639_LAO);
		languageNames.put(273,ELanguageIdentifier.ISO639_OCI);
		languageNames.put(274,ELanguageIdentifier.ISO639_LIJ);
		languageNames.put(275,ELanguageIdentifier.ISO639_MYA);
		languageNames.put(276,ELanguageIdentifier.ISO639_AMH);
		languageNames.put(277,ELanguageIdentifier.ISO639_MDF);
		languageNames.put(278,ELanguageIdentifier.ISO639_ILO);
		languageNames.put(279,ELanguageIdentifier.ISO639_CHV);
		languageNames.put(280,ELanguageIdentifier.ISO639_SMO);
		languageNames.put(281,ELanguageIdentifier.ISO639_UDM);
		languageNames.put(282,ELanguageIdentifier.ISO639_TPI);
		languageNames.put(283,ELanguageIdentifier.ISO639_FRP);
		languageNames.put(284,ELanguageIdentifier.ISO639_SCO);
		languageNames.put(285,ELanguageIdentifier.ISO639_WYM);
		languageNames.put(286,ELanguageIdentifier.ISO639_HAW);
		languageNames.put(287,ELanguageIdentifier.ISO639_CHE);
		languageNames.put(288,ELanguageIdentifier.ISO639_BOS);
		languageNames.put(289,ELanguageIdentifier.ISO639_TON);
		languageNames.put(290,ELanguageIdentifier.ISO639_BQI);
		languageNames.put(291,ELanguageIdentifier.ISO639_PNT);
		languageNames.put(292,ELanguageIdentifier.ISO639_ARZ);
		languageNames.put(293,ELanguageIdentifier.ISO639_PCD);
		languageNames.put(294,ELanguageIdentifier.ISO639_SMA);
		languageNames.put(295,ELanguageIdentifier.ISO639_VRO);
		languageNames.put(296,ELanguageIdentifier.ISO639_QUC);
		languageNames.put(297,ELanguageIdentifier.ISO639_CHF);
		languageNames.put(298,ELanguageIdentifier.ISO639_CTU);
		languageNames.put(299,ELanguageIdentifier.ISO639_CAA);
		languageNames.put(300,ELanguageIdentifier.ISO639_TZH);
		languageNames.put(301,ELanguageIdentifier.ISO639_TZO);
		languageNames.put(302,ELanguageIdentifier.ISO639_COB);
		languageNames.put(303,ELanguageIdentifier.ISO639_HUS);
		languageNames.put(304,ELanguageIdentifier.ISO639_CAC);
		languageNames.put(305,ELanguageIdentifier.ISO639_TOJ);
		languageNames.put(306,ELanguageIdentifier.ISO639_JAC);
		languageNames.put(307,ELanguageIdentifier.ISO639_KJB);
		languageNames.put(308,ELanguageIdentifier.ISO639_KNJ);
		languageNames.put(309,ELanguageIdentifier.ISO639_MHC);
		languageNames.put(310,ELanguageIdentifier.ISO639_AGU);
		languageNames.put(311,ELanguageIdentifier.ISO639_IXL);
		languageNames.put(312,ELanguageIdentifier.ISO639_MAM);
		languageNames.put(313,ELanguageIdentifier.ISO639_TTC);
		languageNames.put(314,ELanguageIdentifier.ISO639_KEK);
		languageNames.put(315,ELanguageIdentifier.ISO639_POC);
		languageNames.put(316,ELanguageIdentifier.ISO639_POH);
		languageNames.put(317,ELanguageIdentifier.ISO639_CAK);
		languageNames.put(318,ELanguageIdentifier.ISO639_ACR);
		languageNames.put(319,ELanguageIdentifier.ISO639_TZJ);
		languageNames.put(320,ELanguageIdentifier.ISO639_QUV);
		languageNames.put(321,ELanguageIdentifier.ISO639_QUM);
		languageNames.put(322,ELanguageIdentifier.ISO639_USP);
		languageNames.put(323,ELanguageIdentifier.ISO639_MOP);
		languageNames.put(324,ELanguageIdentifier.ISO639_LAC);
		languageNames.put(325,ELanguageIdentifier.ISO639_YUA);
		languageNames.put(327,ELanguageIdentifier.ISO639_GMH);
		languageNames.put(328,ELanguageIdentifier.ISO639_GOH);
		languageNames.put(329,ELanguageIdentifier.ISO639_KHK);
		languageNames.put(330,ELanguageIdentifier.ISO639_ACE);
		languageNames.put(331,ELanguageIdentifier.ISO639_RAP);
		languageNames.put(332,ELanguageIdentifier.ISO639_FRO);
		languageNames.put(333,ELanguageIdentifier.ISO639_FRM);
		languageNames.put(334,ELanguageIdentifier.ISO639_HAT);
		languageNames.put(335,ELanguageIdentifier.ISO639_CHU);
		languageNames.put(336,ELanguageIdentifier.ISO639_LTG);
		languageNames.put(337,ELanguageIdentifier.ISO639_ARC);
		languageNames.put(338,ELanguageIdentifier.ISO639_JPA);
		languageNames.put(339,ELanguageIdentifier.ISO639_SYC);
		languageNames.put(340,ELanguageIdentifier.ISO639_TMR);
		languageNames.put(341,ELanguageIdentifier.ISO639_HIL);
		languageNames.put(342,ELanguageIdentifier.ISO639_SGA);
		languageNames.put(343,ELanguageIdentifier.ISO639_KRC);
		languageNames.put(344,ELanguageIdentifier.ISO639_RCF);
		languageNames.put(345,ELanguageIdentifier.ISO639_BNG);
		languageNames.put(346,ELanguageIdentifier.ISO639_PUU);
		//languageNames.put(347,ELanguageIdentifier.ISO639_MYE);
		languageNames.put(348,ELanguageIdentifier.ISO639_KEB);
		languageNames.put(349,ELanguageIdentifier.ISO639_NYA);
		languageNames.put(350,ELanguageIdentifier.ISO639_BEM);
		languageNames.put(351,ELanguageIdentifier.ISO639_UMB);
		languageNames.put(352,ELanguageIdentifier.ISO639_LUN);
		languageNames.put(353,ELanguageIdentifier.ISO639_TOI);
		languageNames.put(354,ELanguageIdentifier.ISO639_SQI);
		languageNames.put(355,ELanguageIdentifier.ISO639_SOT);
		languageNames.put(356,ELanguageIdentifier.ISO639_KAL);
		languageNames.put(357,ELanguageIdentifier.ISO639_KMR);
		languageNames.put(358,ELanguageIdentifier.ISO639_KEA);
		languageNames.put(359,ELanguageIdentifier.ISO639_UGA);
		languageNames.put(360,ELanguageIdentifier.ISO639_RMY);
		languageNames.put(361,ELanguageIdentifier.ISO639_XCL);
		languageNames.put(362,ELanguageIdentifier.ISO639_OTA);
		languageNames.put(363,ELanguageIdentifier.ISO639_ABA);
		languageNames.put(364,ELanguageIdentifier.ISO639_AKR);
		languageNames.put(365,ELanguageIdentifier.ISO639_BIS);
		languageNames.put(366,ELanguageIdentifier.ISO639_FON);
		languageNames.put(367,ELanguageIdentifier.ISO639_FUD);
		languageNames.put(368,ELanguageIdentifier.ISO639_MUL);
		languageNames.put(369,ELanguageIdentifier.ISO639_HOI);
		languageNames.put(370,ELanguageIdentifier.ISO639_UZB);
		languageNames.put(371,ELanguageIdentifier.ISO639_XNO);
		languageNames.put(372,ELanguageIdentifier.ISO639_BCZ);
		languageNames.put(373,ELanguageIdentifier.ISO639_GRC);
		languageNames.put(374,ELanguageIdentifier.ISO639_MLG);
		languageNames.put(375,ELanguageIdentifier.ISO639_XMW);

		if(!languageNames.containsKey(lang)){
			//System.out.println("Language not found: "+lang.getName());
			return "unknown";
		}
		return languageNames.get(lang);
	}



	/**
	 * Maps Wiktionary PartOfSpeech to LMF PartOfSpeech
	 * @param pos
	 */
//	public static EPartOfSpeech mapPos(PartOfSpeech pos){
//		if(pos.equals(PartOfSpeech.NOUN))
//			return EPartOfSpeech.noun;
//		else if(pos.equals(PartOfSpeech.VERB)){
//			return EPartOfSpeech.verb;
//		}else if (pos.equals(PartOfSpeech.ADJECTIVE)){
//			return EPartOfSpeech.adjective;
//		}else if(pos.equals(PartOfSpeech.ADVERB))
//			return EPartOfSpeech.adverb;
//		else if (pos.equals(PartOfSpeech.NUMBER))
//			return EPartOfSpeech.numeral;
//		else if (pos.equals(PartOfSpeech.INTERJECTION))
//			return EPartOfSpeech.interjection;
//		else if(pos.equals(PartOfSpeech.ANSWERING_PARTICLE))
//			return EPartOfSpeech.answerParticle;
//		else if(pos.equals(PartOfSpeech.AUXILIARY_VERB))
//			return EPartOfSpeech.verbAuxiliary;
//		else if(pos.equals(PartOfSpeech.COMPARATIVE_PARTICLE))
//			return EPartOfSpeech.comparativeParticle;
//		else if(pos.equals(PartOfSpeech.DETERMINER))
//			return EPartOfSpeech.determiner;
//		else if(pos.equals(PartOfSpeech.INTERROGATIVE_PRONOUN))
//			return EPartOfSpeech.interrogativePronoun;
//		else if(pos.equals(PartOfSpeech.NEGATIVE_PARTICLE))
//			return EPartOfSpeech.negativeParticle;
//		else if(pos.equals(PartOfSpeech.NUMERAL))
//			return EPartOfSpeech.numeral;
//		else if(pos.equals(PartOfSpeech.PROPER_NOUN))
//			return EPartOfSpeech.nounProper;
//		else if (pos.equals(PartOfSpeech.PREPOSITION))
//			return EPartOfSpeech.preposition;
//		else if (pos.equals(PartOfSpeech.PRONOUN))
//			return EPartOfSpeech.pronoun;
//		else if (pos.equals(PartOfSpeech.CONJUNCTION))
//			return EPartOfSpeech.conjunction;
//		else {
//
//			//System.out.println("CAN't map pos " + pos.name());
//			return null;
//		}
//	}
	/**
	 * Maps Wiktionary Gender to LMF GrammaticalGender
	 * @param gender
	 */
//	public static EGrammaticalGender mapGender(Gender gender){
//		if(gender.equals(Gender.NEUTER))
//			return EGrammaticalGender.neuter;
//		else if(gender.equals(Gender.FEMININE))
//			return EGrammaticalGender.feminine;
//		else return null;
//	}





}
