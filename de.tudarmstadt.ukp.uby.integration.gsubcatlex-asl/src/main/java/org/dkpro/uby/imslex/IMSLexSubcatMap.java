/*******************************************************************************
 * Copyright 2016
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
package org.dkpro.uby.imslex;

import java.util.Map;
import java.util.TreeMap;

public class IMSLexSubcatMap {

	protected Map<String, String> scfArgumentMapping;
	protected Map<String, String> argumentMapping;

	public IMSLexSubcatMap() {
		initializeSCFMapping();
		initializeArgumentMapping();
	}

	protected void initializeSCFMapping() {
		scfArgumentMapping = new TreeMap<String, String>();
		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_an_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");
		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_auf_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_durch_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dadurch");

		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_für_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dafür");

		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_gegen_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dagegen");

		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_über_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darüber");

		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_von_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davon");

		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_vor_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davor");

		scfArgumentMapping.put("subj(NP_nom),corr-obj(PRON_es),obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=es");
		scfArgumentMapping.put("subj(NP_nom),corr(es),obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=es");

		scfArgumentMapping.put("subj(NP_nom),corr-obj(PRON_es),obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=declarativeClause,lexeme=es");

		scfArgumentMapping.put("subj(NP_nom),corr-obj(PRON_es),obj(C_wh)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=subordinateClause,complementizer=whType,lexeme=es");
		scfArgumentMapping.put("subj(NP_nom),corr-obj(PRON_es),obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=es");


		scfArgumentMapping.put("subj(NP_nom),corr-obj(PRON_es),obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=es");
		scfArgumentMapping.put("subj(NP_nom),corr(es),obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=es");

		scfArgumentMapping.put("subj(NP_nom),corr-obj(PRON_es),obj(VP_zu-inf-perf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=past,lexeme=es");

		scfArgumentMapping.put("subj(NP_nom),corr-obj(PRON_es),obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=es");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");
		scfArgumentMapping.put("subj(NP_nom),corr(an_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");

		scfArgumentMapping.put("corr(an_acc),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");
		scfArgumentMapping.put("corr(an_dat),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");


		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_acc),p-obj(C_wh)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=whType,lexeme=daran");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_acc),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=daran");
		scfArgumentMapping.put("subj(NP_nom),corr(an_acc),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=daran");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_acc),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=daran");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");
		scfArgumentMapping.put("subj(NP_nom),corr(an_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_dat),p-obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=daran");
		scfArgumentMapping.put("corr(an_dat),s-comp(C_wh/ob)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=daran");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=daran");
		scfArgumentMapping.put("subj(NP_nom),corr(an_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=daran");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_an_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=daran");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr(auf_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");
		scfArgumentMapping.put("corr(auf_acc),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_acc),p-obj(C_wh)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=whType,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_acc),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr(auf_acc),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darauf");
		scfArgumentMapping.put("corr(auf_acc),s-comp(C_wh/ob)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_acc),p-obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_acc),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr(auf_acc),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_acc),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr(auf_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");
		scfArgumentMapping.put("corr(auf_dat),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_dat),p-obj(C_wh)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=whType,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_dat),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr(auf_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darauf");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_auf_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_aus_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daraus");
		scfArgumentMapping.put("corr(aus_dat),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daraus");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_aus_dat),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=daraus");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_bei_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dabei");
		scfArgumentMapping.put("subj(NP_nom),corr(bei_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dabei");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_bei_dat),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=dabei");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_bei_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=dabei");
		scfArgumentMapping.put("subj(NP_nom),corr(bei_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dabei");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_bei_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dabei");


		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_durch_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dadurch");
		scfArgumentMapping.put("subj(NP_nom),corr(durch_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dadurch");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_für_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dafür");
		scfArgumentMapping.put("subj(NP_nom),corr(für_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dafür");
		scfArgumentMapping.put("corr(für_acc),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dafür");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_für_acc),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=dafür");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_für_acc),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=dafür");
		scfArgumentMapping.put("subj(NP_nom),corr(für_acc),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=dafür");
		scfArgumentMapping.put("corr(für_acc),s-comp(C_wh/ob)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=dafür");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_für_acc),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dafür");
		scfArgumentMapping.put("subj(NP_nom),corr(für_acc),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dafür");
		scfArgumentMapping.put("corr(für_acc),v-comp(V_zu-inf)", "grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dafür");
		scfArgumentMapping.put("corr(für_acc),v-comp(VP_zu-inf)", "grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dafür");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_für_acc),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=dafür");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_gegen_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dagegen");
		scfArgumentMapping.put("subj(NP_nom),corr(gegen_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dagegen");
		scfArgumentMapping.put("corr(gegen_acc),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dagegen");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_gegen_acc),p-obj(VP_zu-inf-perf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=past,lexeme=dagegen");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_gegen_acc),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=dagegen");


		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_in_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darin");
		// darin übereinkommen, dass (in with dative)
		scfArgumentMapping.put("subj(NP_nom),corr(in_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darin");
		// darin einstimmen, dass (in with accusative)
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_in_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darin");

		scfArgumentMapping.put("corr(in_acc),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darin");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_in_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darin");
		scfArgumentMapping.put("subj(NP_nom),corr(in_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darin");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_in_dat),p-obj(VP_zu-inf-perf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=past,lexeme=darin");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_in_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=darin");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_mit_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=damit");
		scfArgumentMapping.put("subj(NP_nom),corr(mit_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=damit");

		scfArgumentMapping.put("subj(NP_nom),arg(PRON_refl-acc),corr-pobj(PAV_mit_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=damit");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_mit_dat),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=damit");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_mit_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=damit");
		scfArgumentMapping.put("subj(NP_nom),corr(mit_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=damit");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_mit_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=damit");

		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_mit_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=object,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=damit");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_nach_dat),p-obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=danach");
		scfArgumentMapping.put("corr(nach_dat),s-comp(C_wh/ob)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=danach");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_nach_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=danach");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_nach_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=danach");
		scfArgumentMapping.put("subj(NP_nom),corr(nach_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=danach");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_nach_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=danach");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_über_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darüber");
		scfArgumentMapping.put("subj(NP_nom),corr(über_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darüber");
		scfArgumentMapping.put("corr(über_acc),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darüber");
		scfArgumentMapping.put("subj(NP_nom),corr(über_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darüber");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_über_acc),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=darüber");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_über_acc),p-obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darüber");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_über_acc),p-obj(C_wh)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=whType,lexeme=darüber");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_über_acc),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darüber");
		scfArgumentMapping.put("subj(NP_nom),corr(über_acc),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darüber");
		scfArgumentMapping.put("corr(über_acc),s-comp(C_wh/ob)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darüber");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_über_acc),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=darüber");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_über_acc),p-obj(VP_zu-inf-perf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=past,lexeme=darüber");

		scfArgumentMapping.put("corr(über_acc),v-comp(V_zu-inf)", "grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darüber");
		scfArgumentMapping.put("corr(über_acc),v-comp(VP_zu-inf)", "grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darüber");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_um_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darum");
		scfArgumentMapping.put("subj(NP_nom),corr(um_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darum");
		scfArgumentMapping.put("corr(um_acc),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darum");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_um_acc),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=darum");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_um_acc),p-obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darum");
		scfArgumentMapping.put("corr(um_acc),s-comp(C_wh/ob)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=darum");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_um_acc),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=darum");
		scfArgumentMapping.put("corr(um_acc),s-comp(V_zu-inf)", "grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darum");
		scfArgumentMapping.put("corr(um_acc),s-comp(VP_zu-inf)", "grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=darum");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_unter_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darunter");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_unter_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=darunter");


		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_von_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davon");
		scfArgumentMapping.put("subj(NP_nom),corr(von_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davon");
		scfArgumentMapping.put("corr(von_dat),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davon");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_von_dat),p-obj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=declarativeClause,lexeme=davon");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_von_dat),p-obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=davon");
		scfArgumentMapping.put("subj(NP_nom),corr(von_dat),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=davon");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_von_dat),p-obj(C_wh)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=whType,lexeme=davon");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_von_dat),p-obj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=davon");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_von_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=davon");
		scfArgumentMapping.put("subj(NP_nom),corr(von_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=davon");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_von_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=davon");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_vor_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davor");
		scfArgumentMapping.put("corr(vor_dat),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davor");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_vor_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=davor");
		scfArgumentMapping.put("subj(NP_nom),corr(vor_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=davor");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_vor_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=davor");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_zu_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dazu");
		scfArgumentMapping.put("subj(NP_nom),corr(zu_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dazu");
		scfArgumentMapping.put("corr(zu_dat),s-comp(C_daß)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dazu");
		scfArgumentMapping.put("corr(zu_dat),s-comp(C_wh/ob)", "grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=dazu");

		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_zu_dat),p-obj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dazu");
		scfArgumentMapping.put("subj(NP_nom),corr(zu_dat),p-obj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,lexeme=dazu");
		scfArgumentMapping.put("subj(NP_nom),corr-pobj(PAV_zu_dat),p-obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=prepositionalComplement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=dazu");

		// the following frames are difficult to realize in terms of word order constraints!!
		// requires implicit knowledge that vp is extraposed
		scfArgumentMapping.put("subj(NP_nom),iobj(NP_dat),corr-obj(PRON_es),obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=es:grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative");

		scfArgumentMapping.put("subj(NP_nom),iobj(PRON_refl-dat),corr-obj(PRON_es),obj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present,lexeme=es:grammaticalFunction=complement,syntacticCategory=reflexive,case=dative");

		scfArgumentMapping.put("subj(NP_nom),iobj(NP_dat),corr-obj(PRON_es),obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=es:grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative");

		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_an_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=daran");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_auf_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darauf");

		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_für_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dafür");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_gegen_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=dagegen");

		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_mit_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=damit");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_nach_dat),p-obj(C_ob)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=yesNoType,lexeme=danach");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_über_acc),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=darüber");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),corr-pobj(PAV_von_dat),p-obj(C_daß)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=prepositionalComplement,syntacticCategory=subordinateClause,complementizer=thatType,lexeme=davon");

		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),v-comp-oc(VP_zu-inf)", "syntacticProperty=objectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),v-comp-oc(VP_zu-inf-perf)", "syntacticProperty=objectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=past");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),v-comp-oc(VP_zu-inf-pres)", "syntacticProperty=objectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present");
		scfArgumentMapping.put("subj(NP_nom),obj(NP_acc),v-comp(V_zu-inf-o)", "syntacticProperty=objectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");

		scfArgumentMapping.put("subj(NP_nom),iobj(NP_dat),v-comp(V_zu-inf-s)", "syntacticProperty=subjectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");
		scfArgumentMapping.put("subj(NP_nom),iobj(NP_dat),v-comp-sc(V_zu-inf-s)", "syntacticProperty=subjectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");
		scfArgumentMapping.put("subj(NP_nom),iobj(NP_dat),v-comp-sc(VP_zu-inf)", "syntacticProperty=subjectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");

		scfArgumentMapping.put("subj(NP_nom),iobj(NP_dat),v-comp(V_zu-inf-o)", "syntacticProperty=objectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");
		scfArgumentMapping.put("subj(NP_nom),iobj(NP_dat),v-comp-oc(VP_zu-inf)", "syntacticProperty=objectControl:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");

		scfArgumentMapping.put("subj(NP_nom),v-comp-r(VP_zu-inf)", "syntacticProperty=subjectRaising:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");
		scfArgumentMapping.put("subj(NP_nom),v-xcomp(V_zu-inf)", "syntacticProperty=subjectRaising:grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative:grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");
	}

	protected void initializeArgumentMapping() {
		argumentMapping = new TreeMap<String, String>();
		argumentMapping.put("v-comp(VP_zu-inf)", "grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");
		argumentMapping.put("v-comp(V_zu-inf)", "grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive");

		argumentMapping.put("v-comp(VP_zu-inf-perf)", "grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=past");
		argumentMapping.put("v-comp(VP_zu-inf-pres)", "grammaticalFunction=complement,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present");

		argumentMapping.put("subj(VP_zu-inf)", "grammaticalFunction=subject,syntacticCategory=verbPhrase,verbForm=toInfinitive");
		argumentMapping.put("subj(V_zu-inf)", "grammaticalFunction=subject,syntacticCategory=verbPhrase,verbForm=toInfinitive");

		argumentMapping.put("subj(VP_zu-inf-perf)", "grammaticalFunction=subject,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=past");
		argumentMapping.put("subj(VP_zu-inf-pres)", "grammaticalFunction=subject,syntacticCategory=verbPhrase,verbForm=toInfinitive,tense=present");

		argumentMapping.put("obj(NP_gen)", "grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=genitive");
		argumentMapping.put("obj(NP_dat)", "grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=dative");

		argumentMapping.put("obj(NP_acc)", "grammaticalFunction=directObject,syntacticCategory=nounPhrase,case=accusative");
		argumentMapping.put("arg(NP_acc)", "grammaticalFunction=complement,syntacticCategory=nounPhrase,case=accusative");
		argumentMapping.put("obj-nopassiv(NP_acc)", "grammaticalFunction=complement,syntacticCategory=nounPhrase,case=accusative");
		argumentMapping.put("arg(PRON_refl-acc)", "grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative");
		argumentMapping.put("obj(PRON_refl-acc)", "grammaticalFunction=complement,syntacticCategory=reflexive,case=accusative");

		argumentMapping.put("mod-dir(AdvP)", "grammaticalFunction=adverbialComplement,syntacticCategory=adverbPhrase,role=directional"); // directional
		argumentMapping.put("mod(AdvP)", "grammaticalFunction=adverbialComplement,syntacticCategory=adverbPhrase");
		argumentMapping.put("mod-loc(AdvP)", "grammaticalFunction=adverbialComplement,syntacticCategory=adverbPhrase,role=locative"); // locative

		argumentMapping.put("iobj(NP_dat)", "grammaticalFunction=complement,syntacticCategory=nounPhrase,case=dative");
		argumentMapping.put("iobj(PRON_refl-dat)", "grammaticalFunction=complement,syntacticCategory=reflexive,case=dative");
		argumentMapping.put("iobj(NP_gen)", "grammaticalFunction=complement,syntacticCategory=nounPhrase,case=genitive");

		argumentMapping.put("subj(NP_dat)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=dative");
		argumentMapping.put("subj(NP_nom)", "grammaticalFunction=subject,syntacticCategory=nounPhrase,case=nominative");
		argumentMapping.put("subj(PRON_dummy)", "grammaticalFunction=subject,syntacticCategory=expletive,lexeme=es");

		argumentMapping.put("p-obj(PP_an_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=an,case=accusative");
		argumentMapping.put("p-obj(an_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=an,case=accusative");
		argumentMapping.put("p-obj(an)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=an");

		argumentMapping.put("p-obj(PP_an_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=an,case=dative");
		argumentMapping.put("p-obj(an_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=an,case=dative");

		argumentMapping.put("p-obj(PP_auf_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=auf,case=accusative");
		argumentMapping.put("p-obj(auf_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=auf,case=accusative");

		argumentMapping.put("p-obj(PP_auf_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=auf,case=dative");
		argumentMapping.put("p-obj(auf_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=auf,case=dative");
		argumentMapping.put("p-obj(auf)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=auf");

		argumentMapping.put("p-obj(PP_aus_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=aus,case=dative");
		argumentMapping.put("p-obj(aus_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=aus,case=dative");

		argumentMapping.put("p-obj(PP_bei_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=bei,case=dative");
		argumentMapping.put("p-obj(bei_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=bei,case=dative");

		argumentMapping.put("p-obj(PP_durch_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=durch,case=accusative");
		argumentMapping.put("p-obj(durch_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=durch,case=accusative");

		argumentMapping.put("p-obj(PP_für_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=für,case=accusative");
		argumentMapping.put("p-obj(für_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=für,case=accusative");

		argumentMapping.put("p-obj(PP_gegen_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=gegen,case=accusative");
		argumentMapping.put("p-obj(gegen_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=gegen,case=accusative");

		argumentMapping.put("p-obj(PP_gegenüber_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=gegenüber,case=dative");

		argumentMapping.put("p-obj(PP_in_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=in,case=accusative");
		argumentMapping.put("p-obj(in_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=in,case=accusative");
		argumentMapping.put("p-obj(in)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=in");

		argumentMapping.put("p-obj(PP_in_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=in,case=dative");
		argumentMapping.put("p-obj(in_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=in,case=dative");

		argumentMapping.put("p-obj(PP_mit_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=mit,case=dative");
		argumentMapping.put("p-obj(mit_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=mit,case=dative");

		argumentMapping.put("p-obj(PP_nach_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=nach,case=dative");
		argumentMapping.put("p-obj(nach_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=nach,case=dative");

		argumentMapping.put("p-obj(PP_neben_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=neben,case=dative");
		argumentMapping.put("p-obj(PP_ohne_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=ohne,case=accusative");

		argumentMapping.put("p-obj(PP_über_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=über,case=accusative");
		argumentMapping.put("p-obj(über_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=über,case=accusative");

		argumentMapping.put("p-obj(ohne_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=ohne,case=accusative");

		argumentMapping.put("p-obj(PP_über_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=über,case=dative");
		argumentMapping.put("p-obj(über_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=über,case=dative");
		argumentMapping.put("p-obj(über)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=über");

		argumentMapping.put("p-obj(PP_um_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=um,case=accusative");
		argumentMapping.put("p-obj(um_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=um,case=accusative");

		argumentMapping.put("p-obj(PP_unter_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=unter,case=dative");
		argumentMapping.put("p-obj(unter_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=unter,case=dative");
		argumentMapping.put("p-obj(unter_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=unter,case=accusative");
		argumentMapping.put("p-obj(PP_unter_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=unter,case=accusative");

		argumentMapping.put("p-obj(PP_von_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=von,case=dative");
		argumentMapping.put("p-obj(von_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=von,case=dative");

		argumentMapping.put("p-obj(PP_vor_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=vor,case=dative");
		argumentMapping.put("p-obj(vor_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=vor,case=dative");
		argumentMapping.put("p-obj(vor_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=vor,case=accusative");
		argumentMapping.put("p-obj(vor)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=vor");
		argumentMapping.put("p-obj(PP_vor_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=vor,case=accusative");

		argumentMapping.put("p-obj(PP_wider_acc)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=wider,case=accusative");

		argumentMapping.put("p-obj(PP_zu_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=zu,case=dative");
		argumentMapping.put("p-obj(zu_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=zu,case=dative");

		argumentMapping.put("p-obj(PP_zwischen_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=zwischen,case=dative");
		argumentMapping.put("p-obj(zwischen_dat)", "grammaticalFunction=prepositionalComplement,syntacticCategory=prepositionalPhrase,preposition=zwischen,case=dative");


		argumentMapping.put("obj-pred(als_Adj)", "grammaticalFunction=objectComplement,syntacticCategory=adjectivePhrase,preposition=als");
		argumentMapping.put("obj-pred(für_Adj)", "grammaticalFunction=objectComplement,syntacticCategory=adjectivePhrase,preposition=für");
		argumentMapping.put("obj-pred(PP_als)", "grammaticalFunction=objectComplement,syntacticCategory=prepositionalPhrase,preposition=als");
		argumentMapping.put("obj-pred(als)", "grammaticalFunction=objectComplement,syntacticCategory=prepositionalPhrase,preposition=als");

		argumentMapping.put("subj-pred(PP_als)", "grammaticalFunction=subjectComplement,syntacticCategory=prepositionalPhrase,preposition=als");

		argumentMapping.put("subj(C_daß)", "grammaticalFunction=subject,syntacticCategory=subordinateClause,complementizer=thatType");
		argumentMapping.put("s-comp(C_daß)", "grammaticalFunction=complement,syntacticCategory=subordinateClause,complementizer=thatType");

		argumentMapping.put("subj(C_wh)", "grammaticalFunction=subject,syntacticCategory=subordinateClause,complementizer=whType");
		argumentMapping.put("s-comp(C_wh)", "grammaticalFunction=complement,syntacticCategory=subordinateClause,complementizer=whType");
		argumentMapping.put("s-comp(C_wh/ob)", "grammaticalFunction=complement,syntacticCategory=subordinateClause,complementizer=yesNoType");
		argumentMapping.put("subj(C_wh/ob)", "grammaticalFunction=subject,syntacticCategory=subordinateClause,complementizer=yesNoType");
		argumentMapping.put("s-comp(C_ob)", "grammaticalFunction=complement,syntacticCategory=subordinateClause,complementizer=yesNoType");
		argumentMapping.put("subj(C_ob)", "grammaticalFunction=subject,syntacticCategory=subordinateClause,complementizer=yesNoType");
		argumentMapping.put("s-comp(C_decl-vsec)", "grammaticalFunction=complement,syntacticCategory=declarativeClause");
		argumentMapping.put("subj(C_decl-vsec)", "grammaticalFunction=subject,syntacticCategory=declarativeClause");

		argumentMapping.put("n-type(mass)", "semanticLabel=massNoun");
		argumentMapping.put("n-type(measure)", "semanticLabel=measureNoun");

		argumentMapping.put("adjtype(adverbial-only)", "syntacticProperty=nonPredicativeAdjective");
		argumentMapping.put("adjtype(adverbial/predicative)", "null");
	}

	protected String createArgumentString(final String subcatLabel) {
		String result = scfArgumentMapping.get(subcatLabel);
		if (result != null)
			return result;

		result = "";
		String[] argParts = subcatLabel.split(",");
		for (String arg : argParts) {
			String part = argumentMapping.get(arg);
			if (part != null)
				result += (result.isEmpty() ? "" : ":") + part;
//			else
//				System.err.println(part);
		}
		return result;
	}

}
